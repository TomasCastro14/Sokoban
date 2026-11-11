package controller;

import model.board.Direction;
import model.board.Visibility;
import model.game.Game;
import model.game.Level;
import model.game.event.BoardListener;
import model.game.event.LevelFlowListener;
import model.game.event.PauseListener;
import model.game.event.TimeListener;
import view.gameplay.HudListener;
import view.gameplay.HudScreen;

import java.util.Objects;

/**
 * MVC controller of the HUD: it keeps the counters and the undo button up to date by listening to the
 * game (Observer), and sends the button presses on. Whether an undo or a pause is allowed is up to the game state.
 */
public final class HudController implements HudListener, BoardListener, LevelFlowListener, PauseListener, TimeListener {
    private final Game game;
    private final HudScreen hud;
    private final Runnable togglePause;
    private Level watched;
    private boolean paused;

    /** togglePause is the same action as ESC, so the button and the key can never disagree. */
    public HudController(Game game, HudScreen hud, Runnable togglePause) {
        this.game = Objects.requireNonNull(game, "game");
        this.hud = Objects.requireNonNull(hud, "hud");
        this.togglePause = Objects.requireNonNull(togglePause, "togglePause");
        game.addBoardListener(this);
        game.addLevelFlowListener(this);
        game.addPauseListener(this);
        hud.setListener(this);
    }

    @Override
    public void onUndoPressed() {
        game.undo();
    }

    @Override
    public void onPausePressed() {
        togglePause.run();
    }

    public void onRestartPressed() {
        game.restart();
    }

    @Override
    public void levelStarted(int levelNumber, Level level) {
        paused = false;
        watch(level);
        hud.showLevelName(level.getName());
        refresh();
    }

    @Override
    public void levelRestarted(int levelNumber, Level level) {
        levelStarted(levelNumber, level);
    }

    @Override
    public void boardRestored(Level level) {
        refresh();
    }

    @Override
    public void playerMoved(Direction direction) {
        refresh();
    }

    @Override
    public void boxPushed(Direction direction) {
        refresh();
    }

    @Override
    public void boxStored(int storedBoxes, int totalBoxes) {
    }

    @Override
    public void doorOpened(Visibility visibility) {
    }

    @Override
    public void secondElapsed(int remainingSeconds) {
        refresh();
    }

    @Override
    public void gamePaused() {
        paused = true;
        refresh();
    }

    @Override
    public void gameResumed() {
        paused = false;
        refresh();
    }

    /** The clock lives in each level, so the HUD moves its time subscription to every new level. */
    private void watch(Level level) {
        if (watched != null) {
            watched.removeTimeListener(this);
        }
        watched = level;
        level.addTimeListener(this);
    }

    /** While paused the undo would be ignored, so the button is shown disabled too. */
    private void refresh() {
        Level level = game.getCurrentLevel();
        hud.showStats(level.getElapsedSeconds(), level.getStats().getPushes(), level.getStats().getMoves());
        hud.setUndoEnabled(level.canUndo() && !paused);
    }
}