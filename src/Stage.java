import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.function.BiConsumer;

public class Stage {

  Grid grid;
  List<Actor> listOfPlayers;
  List<Cell> cellOverlay;
  Optional<Actor> playerInAction;

  GameState currentState;
  Beat beat;

  // Weather system: routes events -> grid behaviour (Strategy-style)
  private final WeatherSystem weatherSystem;

  public Stage() {
    grid = new Grid();
    listOfPlayers = new ArrayList<>();
    cellOverlay = new ArrayList<>();
    playerInAction = Optional.empty();
    currentState = new ChoosingActor();
    beat = new AnimationBeat();

    weatherSystem = new WeatherSystem(grid);
  }

  public void addPlayer(Actor player) {
    listOfPlayers.add(player);
    if (player.isBot()) {
      beat.punchIn(player);
    }
  }

  public void paint(Graphics g, Point mouseLoc) {
    // 1) Update ongoing weather effects each frame
    grid.tickWeather();

    // 2) Existing turn/state logic
    currentState.paint(g, this);

    // 3) Draw grid (cells already include weather colours)
    grid.paint(g, mouseLoc);

    // 4) Blue cell selection overlay with 50% transparency
    grid.paintOverlay(g, cellOverlay, new Color(0f, 0f, 1f, 0.5f));

    // 5) Bot animation beat
    beat.ticktock();

    // 6) Draw all actors
    for (Actor player : listOfPlayers) {
      player.paint(g);
    }

    // 7) Side panel info
    draw_sidepanel(g, mouseLoc);
  }

  private void draw_sidepanel(Graphics g, Point mouseLoc) {
    // lots of magic numbers here
    // they are used to calculate the coordinates of where to draw on the information panel
    final int hTab = 10;
    final int blockVT = 35;
    final int margin = 21 * blockVT;
    int yLoc = 20;

    // state display
    g.setColor(Color.DARK_GRAY);
    g.drawString(currentState.toString(), margin, yLoc);
    yLoc = yLoc + blockVT;

    Optional<Cell> underMouse = grid.cellAtPoint(mouseLoc);
    if (underMouse.isPresent()) {
      Cell hoverCell = underMouse.get();
      g.setColor(Color.DARK_GRAY);
      String coord = String.valueOf(hoverCell.col) + String.valueOf(hoverCell.row);
      g.drawString(coord, margin, yLoc);
    }

    // agent display
    final int vTab = 15;
    final int labelIndent = margin + hTab;
    final int valueIndent = margin + 3 * blockVT;
    yLoc = yLoc + 2 * blockVT;

    for (int i = 0; i < listOfPlayers.size(); i++) {
      Actor a = listOfPlayers.get(i);
      yLoc = yLoc + 2 * blockVT;
      g.drawString(a.getClass().getName(), margin, yLoc);
      g.drawString("location:", labelIndent, yLoc + vTab);
      g.drawString(Character.toString(a.loc.col) + Integer.toString(a.loc.row), valueIndent, yLoc + vTab);
      g.drawString("player type:", labelIndent, yLoc + 2 * vTab);
      g.drawString(a.isBot() ? "Bot" : "Human", valueIndent, yLoc + 2 * vTab);
      if (a.isBot() && a.mover != null) {
        g.drawString("mover:", labelIndent, yLoc + 3 * vTab);
        g.drawString(a.mover.getClass().getName(), valueIndent, yLoc + 3 * vTab);
      }
    }
  }

  public List<Cell> getClearRadius(Cell from, int size) {
    List<Cell> init = grid.getRadius(from, size);
    for (Actor player : listOfPlayers) {
      init.remove(player.loc);
    }
    return init;
  }

  public void mouseClicked(int x, int y) {
    currentState.mouseClick(x, y, this);
  }

  // ===================== WEATHER ENTRY POINT =====================

  /**
   * Called by Client via lambda: stage.applyWeather(event)
   */
  public void applyWeather(WeatherEvent event) {
    weatherSystem.handle(event);
  }

  // ===================== WEATHER SYSTEM (Strategy-style) =====================

  /**
   * Encapsulates mapping from attribute -> behaviour.
   * Uses Map<String, BiConsumer<Grid, WeatherEvent>> as a lightweight Strategy pattern.
   */
  private static class WeatherSystem {

    private final Grid grid;
    private final Map<String, BiConsumer<Grid, WeatherEvent>> effects;

    WeatherSystem(Grid grid) {
      this.grid = grid;
      this.effects = Map.of(
          "rain", Grid::applyRain,
          "temp", Grid::applyTemp
          // You can add "windx" and "windy" later if you implement them
      );
    }

    void handle(WeatherEvent event) {
      BiConsumer<Grid, WeatherEvent> effect = effects.get(event.getAttribute());
      if (effect != null) {
        effect.accept(grid, event);
      }
      // attributes not in the map are safely ignored
    }
  }
}

