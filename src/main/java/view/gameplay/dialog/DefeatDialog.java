package view.gameplay.dialog;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import model.game.loss.LossReason;
import view.components.buttons.ButtonIcon;
import view.components.buttons.RoundStyle;
import view.components.images.UiImage;

/** Shown when the level is lost for good: why it was lost, and leave or try again. */
final class DefeatDialog {
    private static final double TOP_PADDING = 22;
    private static final String TITLE_BOX = "dialog-defeat-title";
    private static final String MESSAGE_BOX = "dialog-defeat-message";
    private static final String MESSAGE_TEXT = "dialog-message";

    private final DialogParts parts;

    DefeatDialog(DialogParts parts) {
        this.parts = parts;
    }

    Node build(LossReason reason, Runnable onLeave, Runnable onRetry) {
        StackPane dialog = parts.panel(TOP_PADDING, parts.overlapped(DialogParts.Overlap.UPPER_IN_FRONT,
                parts.textBox(DialogTexts.DEFEAT_TITLE, TITLE_BOX, DialogParts.LIGHT_TITLE_TEXT),
                parts.textBox(DialogTexts.messageFor(reason), MESSAGE_BOX, MESSAGE_TEXT)));
        parts.addMascot(dialog, MascotPose.OVER_THE_TOP, UiImage.EMOTE_SAD);
        parts.addActions(dialog,
                parts.action(RoundStyle.GREY, ButtonIcon.BACK, onLeave),
                parts.action(RoundStyle.BLUE, ButtonIcon.REPLAY, onRetry));
        return dialog;
    }
}
