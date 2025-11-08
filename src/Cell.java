import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Cell extends Rectangle {
    static int size = 35;
    char col;
    int row;

    // Weather-related state
    private double waterLevel = 0.0; // 0.0 (dry) to 1.0 (flooded)
    private boolean hot = false;

    public Cell(char inCol, int inRow, int x, int y) {
        super(x, y, size, size);
        col = inCol;
        row = inRow;
    }

    public void paint(Graphics g, Point mousePos) {
        // Base colour (no weather)
        Color base = Color.WHITE;

        // Weather: rain makes cell more blue
        if (waterLevel > 0.6) {
            base = Color.BLUE;
        } else if (waterLevel > 0.3) {
            base = new Color(120, 170, 255);
        }

        // Weather: high temperature makes cell hot (orange)
        if (hot) {
            base = Color.ORANGE;
        }

        // Mouse highlight overrides whatever colour we had
        if (contains(mousePos)) {
            base = Color.GRAY;
        }

        g.setColor(base);
        g.fillRect(x, y, size, size);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
    }

    @Override
    public boolean contains(Point p) {
        if (p != null) {
            return super.contains(p);
        } else {
            return false;
        }
    }

    public int leftOfComparison(Cell c) {
        return Integer.compare(col, c.col);
    }

    public int aboveComparison(Cell c) {
        return Integer.compare(row, c.row);
    }

    // ===== Weather helper methods =====

    // Increase/decrease water level safely (0..1)
    public void addWater(double amount) {
        waterLevel = Math.max(0.0, Math.min(1.0, waterLevel + amount));
    }

    // Called each tick to slowly dry out
    public void evaporate() {
        waterLevel = Math.max(0.0, waterLevel - 0.01);
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public boolean isHot() {
        return hot;
    }

    public double getWaterLevel() {
        return waterLevel;
    }
}

