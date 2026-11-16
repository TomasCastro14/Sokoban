package view.gameplay.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonStyle;
import view.components.images.ImageProvider;
import view.components.images.UiImage;

import java.util.Objects;

/**
 * A yes/no question with the look of the level popups: red title and message, the sokoban with an
 * exclamation over the top edge, and the two answers inside the panel. Built from the same parts as
 * the defeat popup, so it can be used outside the game (e.g. the level select) and still match.
 */
public final class ConfirmDialog {
    /** Wider than the level popups: two rectangular answers side by side need the room. */
    private static final double PANEL_WIDTH = 560;
    private static final double ANSWER_SPACING = 24;
    private static final Insets ANSWERS_MARGIN = new Insets(8, 0, 0, 0);
    private static final String PANEL_VARIANT = "dialog-confirm";
    private static final String TITLE_BOX = "dialog-confirm-title";
    private static final String MESSAGE_BOX = "dialog-confirm-message";
    private static final String MESSAGE_TEXT = "dialog-message";
    private static final String STYLESHEET = "/view/dialogs.css";

    private final DialogParts parts;
    private final ButtonProvider buttons;

    public ConfirmDialog(ImageProvider images, ButtonProvider buttons) {
        this.buttons = Objects.requireNonNull(buttons, "buttons");
        this.parts = new DialogParts(images, buttons);
    }

    /** The confirming answer is red: it is the one that cannot be taken back. */
    public Node build(String title, String message, String cancelText, String confirmText,
                      Runnable onCancel, Runnable onConfirm) {
        HBox answers = new HBox(ANSWER_SPACING,
                buttons.createRectangular(cancelText, ButtonStyle.SECONDARY, onCancel),
                buttons.createRectangular(confirmText, ButtonStyle.DANGER, onConfirm));
        answers.setAlignment(Pos.CENTER);
        VBox.setMargin(answers, ANSWERS_MARGIN);

        StackPane dialog = parts.panel(PANEL_WIDTH, PANEL_VARIANT,
                parts.overlapped(DialogParts.Overlap.UPPER_IN_FRONT,
                        parts.textBox(title, TITLE_BOX, DialogParts.LIGHT_TITLE_TEXT),
                        parts.textBox(message, MESSAGE_BOX, MESSAGE_TEXT)),
                answers);
        parts.addMascot(dialog, PANEL_WIDTH, MascotPose.OVER_THE_TOP, UiImage.EMOTE_EXCLAMATION);
        dialog.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLESHEET),
                "Falta " + STYLESHEET).toExternalForm());
        return dialog;
    }
}