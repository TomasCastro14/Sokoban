package view.gameplay.dialog;

import javafx.scene.Node;
import model.game.loss.LossReason;
import model.game.score.Goal;
import model.game.score.ScoreResult;
import view.components.buttons.ButtonProvider;
import view.components.container.DialogLayer;
import view.components.images.ImageProvider;

import java.util.List;

/** Passive view: builds the requested popup and puts it in the layer over the game. */
public final class LevelDialogsView implements LevelDialogs {
    /** The dialogs are built at mockup size; on screen they look better a bit smaller. */
    private static final double DIALOG_SCALE = 0.8;

    private final DialogLayer layer = new DialogLayer(DIALOG_SCALE);
    private final GoalsDialog goals;
    private final DefeatDialog defeat;
    private final VictoryDialog victory;
    private final PauseDialog pause;

    public LevelDialogsView(ImageProvider images, ButtonProvider buttons) {
        DialogParts parts = new DialogParts(images, buttons);
        this.goals = new GoalsDialog(parts);
        this.defeat = new DefeatDialog(parts);
        this.victory = new VictoryDialog(parts);
        this.pause = new PauseDialog(parts);
    }

    public Node getRoot() {
        return layer.getRoot();
    }

    @Override
    public void showGoals(String levelName, List<Goal> levelGoals, Runnable onPlay) {
        layer.show(goals.build(levelName, levelGoals, onPlay));
    }

    @Override
    public void showDefeat(LossReason reason, Runnable onLeave, Runnable onRetry) {
        layer.show(defeat.build(reason, onLeave, onRetry));
    }

    @Override
    public void showVictory(ScoreResult result, Runnable onRetry, Runnable onNext) {
        layer.show(victory.build(result, onRetry, onNext));
    }

    @Override
    public void showPause(Runnable onLeave, Runnable onRestart, Runnable onResume) {
        layer.show(pause.build(onLeave, onRestart), onResume);
    }

    @Override
    public void close() {
        layer.hide();
    }
}