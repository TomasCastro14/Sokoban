package view.components.buttons;

public enum ArrowDirection {
    LEFT(180),
    RIGHT(0);

    private final double rotation;

    ArrowDirection(double rotation) {
        this.rotation = rotation;
    }

    double rotation() {
        return rotation;
    }
}