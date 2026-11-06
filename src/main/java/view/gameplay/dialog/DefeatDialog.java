package view.gameplay.dialog;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.game.loss.LossReason;
import view.components.buttons.ButtonIcon;
import view.components.buttons.RoundStyle;

/** Shown when the level is lost for good: why it was lost, and leave or try again. */
final class DefeatDialog {
    private static final double TOP_PADDING = 18;
    private static final String TITLE_BOX = "dialog-defeat-title";
    private static final String MESSAGE_BOX = "dialog-defeat-message";
    private static final String MESSAGE_TEXT = "dialog-message";

    private final DialogParts parts;

    DefeatDialog(DialogParts parts) {
        this.parts = parts;
    }

    Node build(LossReason reason, Runnable onLeave, Runnable onRetry) {
        VBox banner = new VBox(
                parts.textBox(DialogTexts.DEFEAT_TITLE, TITLE_BOX, DialogParts.LIGHT_TITLE_TEXT),
                parts.textBox(DialogTexts.messageFor(reason), MESSAGE_BOX, MESSAGE_TEXT));
        StackPane dialog = parts.panel(TOP_PADDING, banner);
        parts.addActions(dialog,
                parts.action(RoundStyle.GREY, ButtonIcon.BACK, onLeave),
                parts.action(RoundStyle.BLUE, ButtonIcon.REPLAY, onRetry));
        return dialog;
    }
}
