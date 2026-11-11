package view.gameplay.dialog;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.game.score.GoalResult;
import model.game.score.ScoreResult;
import view.components.buttons.ButtonIcon;
import view.components.buttons.RoundStyle;
import view.components.images.UiImage;

/** Shown when the level is won: stars earned, points and the summary the TPO asks for. */
final class VictoryDialog {
    private static final double STAR_HEIGHT = 140;
    private static final double STAR_SPREAD = 160;
    private static final double STAR_CENTER_Y = 75;
    private static final double MIDDLE_STAR_RISE = 25;
    private static final double SIDE_STAR_TILT = 12;
    private static final String PANEL_VARIANT = "dialog-victory";
    private static final String TITLE_BOX = "dialog-victory-title";
    private static final String SCORE_BOX = "dialog-score";
    private static final String SCORE_TEXT = "dialog-score-text";
    private static final String MET_VALUE = "goal-met";
    private static final String MISSED_VALUE = "goal-missed";

    private final DialogParts parts;

    VictoryDialog(DialogParts parts) {
        this.parts = parts;
    }

    Node build(ScoreResult result, Runnable onRetry, Runnable onNext) {
        Node[] rows = result.goals().stream().map(this::row).toArray(Node[]::new);
        StackPane dialog = parts.panel(PANEL_VARIANT,
                parts.overlapped(DialogParts.Overlap.LOWER_IN_FRONT,
                        parts.textBox(DialogTexts.VICTORY_TITLE, TITLE_BOX, DialogParts.TITLE_TEXT),
                        parts.textBox(DialogTexts.points(result.score()), SCORE_BOX, SCORE_TEXT)),
                parts.list(DialogTexts.SUMMARY_TITLE, rows));
        parts.addMascot(dialog, MascotPose.FROM_THE_RIGHT, UiImage.EMOTE_HEART);
        addStars(dialog, result.stars(), result.goals().size());
        parts.addActions(dialog,
                parts.action(RoundStyle.GREY, ButtonIcon.REPLAY, onRetry),
                parts.action(RoundStyle.GOLD, ButtonIcon.PLAY, onNext));
        return dialog;
    }

    private Node row(GoalResult goal) {
        return parts.row(goal.isMet() ? UiImage.GOAL_MET : UiImage.GOAL_MISSED,
                DialogTexts.nameOf(goal.goal().type()),
                DialogTexts.result(goal.achieved(), goal.goal().limit()),
                goal.isMet() ? MET_VALUE : MISSED_VALUE);
    }

    /**
     * One star per goal along the top edge, earned ones first in gold. The middle one sits higher
     * and the side ones lean outwards, like a podium.
     */
    private void addStars(StackPane dialog, int earned, int total) {
        double middle = (total - 1) / 2.0;
        for (int i = 0; i < total; i++) {
            double offset = i - middle;
            ImageView star = parts.image(i < earned ? UiImage.BIG_STAR_ON : UiImage.BIG_STAR_OFF, STAR_HEIGHT);
            star.setRotate(Math.signum(offset) * SIDE_STAR_TILT);
            double rise = offset == 0 ? MIDDLE_STAR_RISE : 0;
            dialog.getChildren().add(parts.decoration(star,
                    DialogParts.PANEL_WIDTH / 2 + offset * STAR_SPREAD, STAR_CENTER_Y - rise));
        }
    }
}
