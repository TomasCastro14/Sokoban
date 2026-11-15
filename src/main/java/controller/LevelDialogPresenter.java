package controller;

import model.game.Game;
import model.game.Level;
import model.game.LevelStats;
import model.game.event.LevelFlowListener;
import model.game.event.LevelOutcomeListener;
import model.game.event.PauseListener;
import model.game.loss.LossReason;
import model.game.score.ScoreCalculator;
import view.Navigator;
import view.gameplay.dialog.LevelDialogs;

import java.util.Objects;

public final class LevelDialogPresenter implements LevelFlowListener, LevelOutcomeListener, PauseListener {
    private final Game game;
    private final LevelDialogs dialogs;
    private final PlaySession session;
    private final ScoreCalculator scores;
    private final Navigator navigator;

    private LevelDialogPresenter(Game game, LevelDialogs dialogs, PlaySession session,
                                 ScoreCalculator scores, Navigator navigator) {
        this.game = Objects.requireNonNull(game, "game");
        this.dialogs = Objects.requireNonNull(dialogs, "dialogs");
        this.session = Objects.requireNonNull(session, "session");
        this.scores = Objects.requireNonNull(scores, "scores");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
    }

    public static LevelDialogPresenter attach(Game game, LevelDialogs dialogs, PlaySession session,
                                              ScoreCalculator scores, Navigator navigator) {
        LevelDialogPresenter presenter = new LevelDialogPresenter(game, dialogs, session, scores, navigator);
        game.addLevelFlowListener(presenter);
        game.addLevelOutcomeListener(presenter);
        game.addPauseListener(presenter);
        return presenter;
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
    public void gamePaused() {
        dialogs.showPause(navigator::back, game::restart, game::togglePause);
    }

    @Override
    public void gameResumed() {
        dialogs.close();
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
