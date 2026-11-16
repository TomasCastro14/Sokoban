package view.gameplay.dialog;

import model.game.loss.LossReason;
import model.game.score.Goal;
import model.game.score.ScoreResult;

import java.util.List;

/** The popups shown over the game: at the start and end of a level, and while paused. Only one is open at a time. */
public interface LevelDialogs {

    void showGoals(String levelName, List<Goal> goals, Runnable onPlay);

    void showDefeat(LossReason reason, Runnable onLeave, Runnable onRetry);

    /** newRecord: whether this win beat the best result of the level in this session. */
    void showVictory(ScoreResult result, boolean newRecord, Runnable onRetry, Runnable onNext);

    /** onResume runs when the player clicks the dark veil around the popup. */
    void showPause(Runnable onLeave, Runnable onRestart, Runnable onResume);

    void close();
}