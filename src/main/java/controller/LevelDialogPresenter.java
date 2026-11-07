package controller;

import model.game.Game;
import model.game.Level;
import model.game.LevelStats;
import model.game.event.LevelFlowListener;
import model.game.event.LevelOutcomeListener;
import model.game.loss.LossReason;
import model.game.score.ScoreCalculator;
import view.Navigator;
import view.gameplay.dialog.LevelDialogs;

import java.util.Objects;

/**
 * Decides which popup the player sees and holds play while one is open:
 * the goals before a level starts, the defeat when a loss is final and the summary on a win.
 * The popups close because the game moved on (a level started or restarted), not because a button was pressed.
 */
public final class LevelDialogPresenter implements LevelFlowListener, LevelOutcomeListener {
    private final Game game;
    private final LevelDialogs dialogs;
    private final PlaySession session;
    private final ScoreCalculator scores;
    private final Navigator navigator;

    public LevelDialogPresenter(Game game, LevelDialogs dialogs, PlaySession session,
                                ScoreCalculator scores, Navigator navigator) {
        this.game = Objects.requireNonNull(game, "game");
        this.dialogs = Objects.requireNonNull(dialogs, "dialogs");
        this.session = Objects.requireNonNull(session, "session");
        this.scores = Objects.requireNonNull(scores, "scores");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        game.addLevelFlowListener(this);
        game.addLevelOutcomeListener(this);
    }

    @Override
    public void levelStarted(int levelNumber, Level level) {
        session.hold();
        dialogs.showGoals(level.getName(), level.getGoals().all(), this::play);
    }

    @Override
    public void levelRestarted(int levelNumber, Level level) {
        play();
    }

    @Override
    public void boardRestored(Level level) {
    }

    @Override
    public void levelWon(LevelStats stats) {
        session.stop();
        dialogs.showVictory(game.getCurrentLevel().score(scores), game::restart, game::nextLevel);
    }

    /** A loss that undo can still fix (a deadlock with undos left) keeps the level going. */
    @Override
    public void levelLost(LossReason reason) {
        if (game.getCurrentLevel().canRecoverFrom(reason)) {
            return;
        }
        session.stop();
        dialogs.showDefeat(reason, navigator::back, game::restart);
    }

    private void play() {
        dialogs.close();
        session.resume();
    }
}
