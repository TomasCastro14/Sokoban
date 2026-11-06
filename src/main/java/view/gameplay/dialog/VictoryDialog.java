package view.gameplay.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.game.score.GoalResult;
import model.game.score.ScoreResult;
import view.components.buttons.ButtonIcon;
import view.components.buttons.RoundStyle;
import view.components.images.UiImage;

/** Shown when the level is won: stars earned, points and the summary the TPO asks for. */
final class VictoryDialog {
    private static final double STAR_HEIGHT = 72;
    private static final double MIDDLE_STAR_RISE = 16;
    private static final double STARS_OVERHANG = 44;
    private static final double TOP_PADDING = STAR_HEIGHT - STARS_OVERHANG + 18;
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
        VBox score = new VBox(
                parts.textBox(DialogTexts.VICTORY_TITLE, TITLE_BOX, DialogParts.TITLE_TEXT),
                parts.textBox(DialogTexts.points(result.score()), SCORE_BOX, SCORE_TEXT));
        Node[] rows = result.goals().stream().map(this::row).toArray(Node[]::new);

        StackPane dialog = parts.panel(TOP_PADDING, score, parts.list(DialogTexts.SUMMARY_TITLE, rows));
        dialog.getChildren().add(stars(result.stars(), result.goals().size()));
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

    /** One star per goal, earned ones first in gold; the middle one sits higher, like a podium. */
    private Node stars(int earned, int total) {
        HBox row = new HBox();
        row.setAlignment(Pos.BOTTOM_CENTER);
        for (int i = 0; i < total; i++) {
            ImageView star = parts.image(i < earned ? UiImage.BIG_STAR_ON : UiImage.BIG_STAR_OFF, STAR_HEIGHT);
            if (i == total / 2) {
                star.setTranslateY(-MIDDLE_STAR_RISE);
            }
            row.getChildren().add(star);
        }
        row.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        row.setMouseTransparent(true);
        StackPane.setAlignment(row, Pos.TOP_CENTER);
        StackPane.setMargin(row, new Insets(-STARS_OVERHANG, 0, 0, 0));
        return row;
    }
}
