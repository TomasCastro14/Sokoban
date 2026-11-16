package view.gameplay.dialog;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import view.components.buttons.ButtonIcon;
import view.components.buttons.RoundStyle;
import view.components.images.UiImage;

/** Shown while the game is paused: the sokoban waiting inside the box, and leave or start over. */
final class PauseDialog {
    private static final String PANEL_VARIANT = "dialog-pause";
    private static final double MASCOT_HEIGHT = 110;
    private static final double BUBBLE_HEIGHT = 62;
    /** The sokoban stands a little below the middle of the box, as in the mockup. */
    private static final double MASCOT_DROP = 25;
    /** Where the thought bubble sits from the sokoban's center: up and to his left. */
    private static final double BUBBLE_OFFSET_X = -60;
    private static final double BUBBLE_OFFSET_Y = -97;
    private static final String TITLE_BOX = "dialog-pause-title";
    private static final String SCENE_BOX = "dialog-pause-scene";

    private final DialogParts parts;

    PauseDialog(DialogParts parts) {
        this.parts = parts;
    }

    Node build(Runnable onLeave, Runnable onRestart) {
        StackPane dialog = parts.panel(PANEL_VARIANT, parts.overlapped(DialogParts.Overlap.UPPER_IN_FRONT,
                parts.textBox(DialogTexts.PAUSE_TITLE, TITLE_BOX, DialogParts.LIGHT_TITLE_TEXT),
                scene()));
        parts.addActions(dialog,
                parts.action(RoundStyle.GREY, ButtonIcon.BACK, onLeave),
                parts.action(RoundStyle.BLUE, ButtonIcon.REPLAY, onRestart));
        return dialog;
    }

    /** Translations move the pictures without touching the layout, so the box keeps its CSS size. */
    private StackPane scene() {
        ImageView mascot = parts.image(UiImage.MASCOT, MASCOT_HEIGHT);
        mascot.setTranslateY(MASCOT_DROP);
        ImageView bubble = parts.image(UiImage.EMOTE_EXCLAMATION, BUBBLE_HEIGHT);
        bubble.setTranslateX(BUBBLE_OFFSET_X);
        bubble.setTranslateY(MASCOT_DROP + BUBBLE_OFFSET_Y);

        StackPane box = new StackPane(mascot, bubble);
        box.getStyleClass().add(SCENE_BOX);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }
}