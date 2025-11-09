public class WeatherEvent {
    private final long timestamp;
    private final String attribute; // "rain", "windx", "windy", "temp"
    private final int x;
    private final int y;
    private final double value; // 0.0 - 1.0

    public WeatherEvent(long timestamp, String attribute, int x, int y, double value) {
        this.timestamp = timestamp;
        this.attribute = attribute;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public long getTimestamp() { return timestamp; }
    public String getAttribute() { return attribute; }
    public int getX() { return x; }
    public int getY() { return y; }
    public double getValue() { return value; }

    @Override
    public String toString() {
        return "WeatherEvent{" +
                "ts=" + timestamp +
                ", attr='" + attribute + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", v=" + value +
                '}';
    }
}
