package service.sound;

public record Volume(double level) {
    public static final double MIN = 0.0;
    public static final double MAX = 1.0;
    public static final Volume DEFAULT = new Volume(0.8);

    public Volume {
        if (Double.isNaN(level) || level < MIN || level > MAX) {
            throw new IllegalArgumentException(
                    "Volume must be between " + MIN + " and " + MAX + ", got " + level);
        }
    }
}
