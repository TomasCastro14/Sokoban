package view.components.container;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/** A popup slot on top of a screen: one dialog at a time over the dark veil, or nothing at all. */
public final class DialogLayer {
    private static final Duration FADE_IN = Duration.millis(160);
    private static final double HIDDEN = 0;
    private static final double SHOWN = 1;

    private final StackPane root = new StackPane();
    private final Region overlay = Overlay.create();

    public DialogLayer() {
        root.setVisible(false);
    }

    public Node getRoot() {
        return root;
    }

    public void show(Node dialog) {
        root.getChildren().setAll(overlay, dialog);
        root.setVisible(true);
        root.setOpacity(HIDDEN);
        FadeTransition fade = new FadeTransition(FADE_IN, root);
        fade.setToValue(SHOWN);
        fade.play();
    }

    public void hide() {
        root.getChildren().clear();
        root.setVisible(false);
    }
}
