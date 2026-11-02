package controller;
import javafx.application.Platform;
import javafx.scene.Scene;
import model.game.Game;
import model.game.Level;
import model.game.LevelStatus;
import model.game.event.GameEvent;
import model.game.event.GameListener;
import view.gameplay.GameScreen;
import view.gameplay.ScreenLifecycleListener;

import java.util.Objects;

public final class GamePresenter implements GameListener, ScreenLifecycleListener {
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
        game.addListener(this);
        screen.setLifecycleListener(this);
    }

    public void dispose() {
        screenHidden();
        clock.stop();
        game.removeListener(this);
    }

    @Override
    public void screenShown(Scene scene) {
        visible = true;
        unbindKeys = input.bindKeys(scene);
        Level level = game.getCurrentLevel();
        screen.showLevel(level);
        if (level.getStatus() == LevelStatus.PLAYING) {
            clock.start();
        }
    }

    @Override
    public void screenHidden() {
        visible = false;
        unbindKeys.run();
        unbindKeys = () -> { };
        clock.pause();
    }

    @Override
    public void onEvent(GameEvent event) {
        if (!visible) {
            return;
        }
        switch (event.getType()) {
            case LEVEL_STARTED -> {
                screen.showLevel(game.getCurrentLevel());
                clock.start();
            }
            case BOARD_RESTORED -> screen.showLevel(game.getCurrentLevel());
            case PLAYER_MOVED -> screen.playerMoved();
            case BOX_PUSHED, BOX_STORED, DOOR_OPENED -> screen.boardChanged();
            case LEVEL_WON -> {
                clock.stop();
                Platform.runLater(game::nextLevel);
            }
            case LEVEL_LOST -> clock.stop();
            default -> { }
        }
    }
}