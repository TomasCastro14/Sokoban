package controller;

import javafx.scene.Scene;
import model.board.Direction;
import model.board.Visibility;
import model.game.Game;
import model.game.Level;
import model.game.LevelStats;
import model.game.event.BoardListener;
import model.game.event.LevelFlowListener;
import model.game.event.LevelOutcomeListener;
import model.game.event.PauseListener;
import model.game.loss.LossReason;
import view.gameplay.GameScreen;
import view.gameplay.ScreenLifecycleListener;

import java.util.Objects;

public final class GamePresenter
        implements BoardListener, LevelFlowListener, LevelOutcomeListener, PauseListener, ScreenLifecycleListener {
    private final Game game;
    private final GameScreen screen;
    private final InputController input;
    private final GameClock clock;
    private Runnable unbindKeys = () -> { };
    private boolean visible;

    public GamePresenter(Game game, GameScreen screen, InputController input, GameClock clock) {
        this.game = Objects.requireNonNull(game, "game");
        this.screen = Objects.requireNonNull(screen, "screen");
        this.input = Objects.requireNonNull(input, "input");
        this.clock = Objects.requireNonNull(clock, "clock");
        game.addBoardListener(this);
        game.addLevelFlowListener(this);
        game.addLevelOutcomeListener(this);
        game.addPauseListener(this);
        screen.setLifecycleListener(this);
    }

    public void dispose() {
        screenHidden();
        clock.stop();
        game.removeBoardListener(this);
        game.removeLevelFlowListener(this);
        game.removeLevelOutcomeListener(this);
        game.removePauseListener(this);
    }

    public void onEscape() {
        game.togglePause();
    }

    @Override
    public void screenShown(Scene scene) {
        visible = true;
        unbindKeys = input.bindKeys(scene);
        screen.showLevel(game.getCurrentLevel());
        clock.start();
    }

    @Override
    public void screenHidden() {
        visible = false;
        unbindKeys.run();
        unbindKeys = () -> { };
        clock.pause();
    }

    @Override
    public void levelStarted(int levelNumber, Level level) {
        screen.showPaused(false);
        if (visible) {
            screen.showLevel(level);
            clock.start();
        }
    }

    @Override
    public void levelRestarted(int levelNumber, Level level) {
        levelStarted(levelNumber, level);
    }

    @Override
    public void boardRestored(Level level) {
        if (visible) {
            screen.showLevel(level);
        }
    }

    @Override
    public void playerMoved(Direction direction) {
        if (visible) {
            screen.playerMoved();
        }
    }

    @Override
    public void boxPushed(Direction direction) {
        if (visible) {
            screen.boardChanged();
        }
    }

    @Override
    public void boxStored(int storedBoxes, int totalBoxes) {
        if (visible) {
            screen.boardChanged();
        }
    }

    @Override
    public void doorOpened(Visibility visibility) {
        if (visible) {
            screen.boardChanged();
        }
    }

    @Override
    public void levelWon(LevelStats stats) {
        if (visible) {
            clock.stop();
        }
    }

    @Override
    public void levelLost(LossReason reason) {
        if (visible) {
            clock.stop();
        }
    }

    @Override
    public void gamePaused() {
        screen.showPaused(true);
    }

    @Override
    public void gameResumed() {
        screen.showPaused(false);
    }
}