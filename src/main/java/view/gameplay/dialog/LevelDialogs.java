package view.gameplay.dialog;

import model.game.loss.LossReason;
import model.game.score.Goal;
import model.game.score.ScoreResult;

import java.util.List;

/** The popups shown over the game at the start and at the end of a level. Only one is open at a time. */
public interface LevelDialogs {

    void showGoals(String levelName, List<Goal> goals, Runnable onPlay);

    void showDefeat(LossReason reason, Runnable onLeave, Runnable onRetry);

    void showVictory(ScoreResult result, Runnable onRetry, Runnable onNext);

    void close();
}
