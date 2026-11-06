package view.components.buttons;

/** Diameter of a round button; its icon scales with it. */
public enum ButtonSize {
    SMALL(56),
    REGULAR(72),
    LARGE(88);

    private final double diameter;

    ButtonSize(double diameter) {
        this.diameter = diameter;
    }

    public double diameter() {
        return diameter;
    }
}
