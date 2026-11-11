package view.gameplay.dialog;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import model.game.score.Goal;
import view.components.buttons.ButtonIcon;
import view.components.buttons.RoundStyle;
import view.components.images.UiImage;

import java.util.List;

/** Shown before playing: the level name and what each of the three stars asks for. */
final class GoalsDialog {
    private static final String PANEL_VARIANT = "dialog-goals";
    private static final String NAME_BOX = "dialog-name";
    private static final String NEUTRAL_VALUE = "goal-neutral";

    private final DialogParts parts;

    GoalsDialog(DialogParts parts) {
        this.parts = parts;
    }

    Node build(String levelName, List<Goal> goals, Runnable onPlay) {
        Node[] rows = goals.stream().map(this::row).toArray(Node[]::new);
        StackPane dialog = parts.panel(PANEL_VARIANT,
                parts.textBox(levelName, NAME_BOX, DialogParts.TITLE_TEXT),
                parts.list(DialogTexts.GOALS_TITLE, rows));
        parts.addMascot(dialog, MascotPose.OVER_THE_TOP, UiImage.EMOTE_HAPPY);
        parts.addActions(dialog, parts.action(RoundStyle.GOLD, ButtonIcon.PLAY, onPlay));
        return dialog;
    }

    private Node row(Goal goal) {
        return parts.row(UiImage.STAR_ON, DialogTexts.nameOf(goal.type()), String.valueOf(goal.limit()), NEUTRAL_VALUE);
    }
}
