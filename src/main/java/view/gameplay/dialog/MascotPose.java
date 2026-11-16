package view.gameplay.dialog;

/**
 * Where the sokoban peeks out from behind a popup. Offsets are in pixels from an anchor on the
 * panel's top edge (a fraction of its width), measured on the design mockups.
 */
enum MascotPose {
    /** Head over the top edge, a little right of center (goals and defeat). */
    OVER_THE_TOP(0.5, 15, -50, 0, -86, -69),
    /** Leaning out from behind the right edge (victory). */
    FROM_THE_RIGHT(1.0, 60, 200, 40, 86, -94);

    private final double anchorRatio;
    private final double figureX;
    private final double figureY;
    private final double rotation;
    private final double bubbleX;
    private final double bubbleY;

    MascotPose(double anchorRatio, double figureX, double figureY, double rotation, double bubbleX, double bubbleY) {
        this.anchorRatio = anchorRatio;
        this.figureX = figureX;
        this.figureY = figureY;
        this.rotation = rotation;
        this.bubbleX = bubbleX;
        this.bubbleY = bubbleY;
    }

    double figureCenterX(double panelWidth) {
        return panelWidth * anchorRatio + figureX;
    }

    double figureCenterY() {
        return figureY;
    }

    double rotation() {
        return rotation;
    }

    /** The thought bubble is placed relative to the figure, so it follows it. */
    double bubbleCenterX(double panelWidth) {
        return figureCenterX(panelWidth) + bubbleX;
    }

    double bubbleCenterY() {
        return figureY + bubbleY;
    }
}
