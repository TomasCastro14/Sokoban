package view.components.container;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Objects;

/**
 * A popup slot on top of a screen: one dialog at a time over the dark veil, or nothing at all.
 * Dialogs are built at one fixed design size and the layer scales them as a whole, so every
 * part keeps its proportions and a dialog always fits in the window, whatever its size.
 */
public final class DialogLayer {
    private static final Duration FADE_IN = Duration.millis(160);
    private static final double HIDDEN = 0;
    private static final double SHOWN = 1;
    /** Share of the window a dialog may take at most, so it never touches the edges. */
    private static final double MAX_SCREEN_SHARE = 0.9;
    private static final Runnable NOTHING = () -> { };

    private final StackPane root = new StackPane();
    private final Region overlay = Overlay.create();
    private final double preferredScale;

    /** @param preferredScale how big dialogs are shown when the window has room (1 = design size) */
    public DialogLayer(double preferredScale) {
        if (preferredScale <= 0) {
            throw new IllegalArgumentException("The scale must be greater than zero, got " + preferredScale);
        }
        this.preferredScale = preferredScale;
        root.setVisible(false);
    }

    public Node getRoot() {
        return root;
    }

    public void show(Node dialog) {
        show(dialog, NOTHING);
    }

    /** Like show(dialog), but a click on the veil around the dialog runs onVeilClicked (the pause closes that way). */
    public void show(Node dialog, Runnable onVeilClicked) {
        Objects.requireNonNull(onVeilClicked, "onVeilClicked");
        overlay.setOnMouseClicked(event -> onVeilClicked.run());
        fitToWindow(dialog);
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

    /** Measured with its decorations (stars, sokoban), which stick out of the dialog's layout. */
    private void fitToWindow(Node dialog) {
        DoubleBinding scale = Bindings.createDoubleBinding(() -> {
            double width = dialog.getBoundsInLocal().getWidth();
            double height = dialog.getBoundsInLocal().getHeight();
            if (width <= 0 || height <= 0 || root.getWidth() <= 0 || root.getHeight() <= 0) {
                return preferredScale;
            }
            double fitting = Math.min(root.getWidth() / width, root.getHeight() / height) * MAX_SCREEN_SHARE;
            return Math.min(preferredScale, fitting);
        }, root.widthProperty(), root.heightProperty(), dialog.boundsInLocalProperty());
        dialog.scaleXProperty().bind(scale);
        dialog.scaleYProperty().bind(scale);
    }
}
