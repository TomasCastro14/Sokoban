package controller;

import javafx.scene.Scene;
import model.game.Game;
import model.game.Level;
import model.game.LevelStatus;
import model.game.event.GameEvent;
import model.game.event.GameListener;
import view.gameplay.GameScreen;
import view.gameplay.ScreenLifecycleListener;

import java.util.Objects;

/**
 * MVC controller of the game screen. Observer of the model (GameListener) and listener of the view
 * (ScreenLifecycleListener): it is the only place where the two meet. The view never sees events or
 * Game; the model never sees the view. It also owns what depends on the screen being visible
 * (keyboard and clock), so a game running behind the menus can neither be moved nor ticked.
 */
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

    @Override
    public void screenShown(Scene scene) {
        visible = true;
        input.bindKeys(scene);
        // Whatever happened while the screen was hidden is read from the model now.
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
        clock.stop();
    }

    @Override
    public void onEvent(GameEvent event) {
        if (!visible) {
            return;      // hidden screens do not react; screenShown reads the current state
        }
        switch (event.getType()) {
            case LEVEL_STARTED -> {
                screen.showLevel(game.getCurrentLevel());
                clock.start();
            }
            case BOARD_RESTORED -> screen.showLevel(game.getCurrentLevel());
            case PLAYER_MOVED -> screen.playerMoved();
            case BOX_PUSHED, BOX_STORED, DOOR_OPENED -> screen.boardChanged();
            case LEVEL_WON, LEVEL_LOST -> clock.stop();   // the summary screen takes over from here
        }
    }
}
