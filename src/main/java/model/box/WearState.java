package model.box;

public enum WearState {
    CRITICAL,
    LOW,
    MEDIUM,
    HIGH;

    private static final int CRITICAL_BELOW = 2;
    private static final int THIRDS = 3;

    public static WearState of(int remainingPushes) {
        if (remainingPushes < CRITICAL_BELOW) {
            return CRITICAL;
        }
        int third = (remainingPushes * THIRDS + WeakBox.MAX_PUSHES - 1) / WeakBox.MAX_PUSHES;   // ceil: 1..3
        return switch (third) {
            case 1 -> LOW;
            case 2 -> MEDIUM;
            default -> HIGH;
        };
    }
}