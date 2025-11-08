import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Stream;

public class Grid {

  // 20x20 grid: first index = column (A..), second index = row (0..)
  Cell[][] cells = new Cell[20][20];

  public Grid() {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        cells[i][j] = new Cell(colToLabel(i), j, 10 + Cell.size * i, 10 + Cell.size * j);
      }
    }
  }

  private char colToLabel(int col) {
    return (char) (col + Character.valueOf('A'));
  }

  private int labelToCol(char col) {
    return (int) (col - Character.valueOf('A'));
  }

  // ==================== Painting ====================

  public void paint(Graphics g, Point mousePos) {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        cells[i][j].paint(g, mousePos);
      }
    }
  }

  public void paintOverlay(Graphics g, List<Cell> cells, Color color) {
    g.setColor(color);
    for (Cell c : cells) {
      g.fillRect(c.x + 2, c.y + 2, c.width - 4, c.height - 4);
    }
  }

  // ==================== Cell lookup helpers ====================

  public Optional<Cell> cellAtColRow(int c, int r) {
    if (c >= 0 && c < cells.length && r >= 0 && r < cells[c].length) {
      return Optional.of(cells[c][r]);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Cell> cellAtColRow(char c, int r) {
    return cellAtColRow(labelToCol(c), r);
  }

  public Optional<Cell> cellAtPoint(Point p) {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        if (cells[i][j].contains(p)) {
          return Optional.of(cells[i][j]);
        }
      }
    }
    return Optional.empty();
  }

  public List<Cell> getRadius(Cell from, int size) {
    int i = labelToCol(from.col);
    int j = from.row;
    Set<Cell> inRadius = new HashSet<>();

    if (size > 0) {
      cellAtColRow(colToLabel(i), j - 1).ifPresent(inRadius::add);
      cellAtColRow(colToLabel(i), j + 1).ifPresent(inRadius::add);
      cellAtColRow(colToLabel(i - 1), j).ifPresent(inRadius::add);
      cellAtColRow(colToLabel(i + 1), j).ifPresent(inRadius::add);
    }

    for (Cell c : inRadius.toArray(new Cell[0])) {
      inRadius.addAll(getRadius(c, size - 1));
    }
    return new ArrayList<>(inRadius);
  }

  // Flatten 2D array -> Stream<Cell> (for lambdas/streams work)
  public Stream<Cell> allCells() {
    return Arrays.stream(cells).flatMap(Arrays::stream);
  }

  // ==================== Weather mapping helpers ====================

  /**
   * Convert weather coordinates (0,0 at centre) into our grid indices.
   */
  private Optional<Cell> cellAtWeatherCoord(WeatherEvent e) {
    int cols = cells.length;
    int rows = cells[0].length;

    int centerCol = cols / 2; // 10 for 20x20
    int centerRow = rows / 2; // 10 for 20x20

    int gridCol = e.getX() + centerCol;
    int gridRow = e.getY() + centerRow;

    if (gridCol >= 0 && gridCol < cols && gridRow >= 0 && gridRow < rows) {
      return Optional.of(cells[gridCol][gridRow]);
    }
    return Optional.empty(); // outside grid
  }

  // ==================== Weather behaviours (called from Stage) ====================

  // Rain: increase water level at the target cell
  public void applyRain(WeatherEvent e) {
    cellAtWeatherCoord(e)
        .ifPresent(cell -> cell.addWater(e.getValue()));
  }

  // Temperature: strong heat affects cells in a radius around the event
  public void applyTemp(WeatherEvent e) {
    if (e.getValue() > 0.7) {
      cellAtWeatherCoord(e).ifPresent(center -> {
        // Use existing getRadius to find neighbours (~3 cells away)
        List<Cell> affected = getRadius(center, 3);
        affected.forEach(c -> c.setHot(true));
      });
    }
  }

  /**
   * Called regularly (e.g. each frame) to let weather effects decay over time.
   */
  public void tickWeather() {
    allCells().forEach(c -> {
      c.evaporate();
      if (c.getWaterLevel() == 0.0) {
        c.setHot(false);
      }
    });
  }
}

