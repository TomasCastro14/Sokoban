package view.components.container;

import javafx.scene.layout.Region;

/** The dark veil every popup puts between itself and the screen behind it. */
final class Overlay {
    private static final String STYLE = "popup-overlay";

    private Overlay() {
    }

    static Region create() {
        Region overlay = new Region();
        overlay.getStyleClass().add(STYLE);
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return overlay;
    }
}
