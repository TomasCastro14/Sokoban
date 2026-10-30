package model.game;

import model.board.Direction;
import model.game.event.GameEvent;
import model.game.event.GameListener;
import model.game.event.EventType;
import model.game.loss.LossReason;
import model.game.state.BlockedState;
import model.game.state.GameCompletedState;
import model.game.state.GameState;
import model.game.state.LostState;
import model.game.state.MenuState;
import model.game.state.PlayingState;
import model.game.state.WonState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Facade of the model: controllers talk to the game and never to the level, and the current
 * {@link GameState} decides what is allowed (State). It is also the Observer subject of the
 * controllers: it republishes the events of whichever {@link Level} is current, so a listener
 * subscribes once and keeps working across restarts and level changes.
 *
 * The state is brought up to date <em>before</em> an event is republished, so a listener that
 * reads {@link #getState()} while handling LEVEL_WON or LEVEL_LOST already sees the new state.
 * Meant to be used from a single thread (the JavaFX application thread).
 */
public class Game {
    private final IntFunction<Level> levelFactory;
    private final int levelCount;
    private final List<GameListener> listeners = new ArrayList<>();
    private GameState state = new MenuState();
    private Level level;
    private int levelNumber;

    /**
     * Builds nothing yet: the first level is created by {@link #start()}.
     *
     * @param levelFactory builds a fresh level from its number (1-based); called again to restart
     * @param levelCount   number of levels in the game
     */
    public Game(IntFunction<Level> levelFactory, int levelCount) {
        if (levelCount < 1) {
            throw new IllegalArgumentException("The game needs at least one level, got " + levelCount);
        }
        this.levelFactory = Objects.requireNonNull(levelFactory, "levelFactory");
        this.levelCount = levelCount;
    }

    public void addListener(GameListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public GameState getState() {
        return state;
    }

    /** @throws IllegalStateException before the game has started */
    public Level getCurrentLevel() {
        if (level == null) {
            throw new IllegalStateException("The game has not started: call start() first");
        }
        return level;
    }

    /** 1-based number of the current level, or 0 before the game has started. */
    public int getCurrentLevelNumber() {
        return levelNumber;
    }

    public int getLevelCount() {
        return levelCount;
    }

    /** Starts the game at the first level. */
    public void start() {
        start(1);
    }

    /** Starts the game at the given level (1-based), whatever state it was in. */
    public void start(int number) {
        if (number < 1 || number > levelCount) {
            throw new IllegalArgumentException("Level " + number + " is out of range 1.." + levelCount);
        }
        load(number);
        publish(new GameEvent(EventType.LEVEL_STARTED));
    }

    /** Ignored while the state does not accept input (menu, won, lost, blocked, completed). */
    public void move(Direction direction) {
        if (state.acceptsInput()) {
            level.move(direction);
        }
    }

    /** Takes the last move back, if the state allows it and the level has one left to undo. */
    public void undo() {
        if (state.allowsUndo() && level.undo()) {
            state = stateFor(level);       // a blocked level is playable again
            publish(new GameEvent(EventType.BOARD_RESTORED));
        }
    }

    /** Rebuilds the current level from its source, as if it were new. */
    public void restart() {
        if (state.allowsRestart()) {
            load(levelNumber);
            publish(new GameEvent(EventType.LEVEL_STARTED));
        }
    }

    /** From a won level, loads the next one; after the last one the game is completed. */
    public void nextLevel() {
        if (!state.allowsNextLevel()) {
            return;
        }
        if (levelNumber == levelCount) {
            state = new GameCompletedState();
            return;
        }
        load(levelNumber + 1);
        publish(new GameEvent(EventType.LEVEL_STARTED));
    }

    /** One second of play time has passed. Time only runs while the player can act. */
    public void tick() {
        if (state.acceptsInput()) {
            level.elapseSeconds(1);
        }
    }

    public boolean isInputBlocked() {
        return !state.acceptsInput();
    }

    private void load(int number) {
        Level next = levelFactory.apply(number);
        if (next == null) {
            throw new IllegalStateException("No level was built for number " + number);
        }
        // A level that was replaced can no longer change the game, even if someone still holds it.
        next.addListener(event -> {
            if (next == level) {
                onLevelEvent(event);
            }
        });
        level = next;
        levelNumber = number;
        state = stateFor(next);
    }

    private void onLevelEvent(GameEvent event) {
        switch (event.getType()) {
            case LEVEL_WON, LEVEL_LOST -> state = stateFor(level);
            default -> { }
        }
        publish(event);
    }

    /** A deadlock is recoverable with an undo (blocked); every other loss ends the attempt (lost). */
    private static GameState stateFor(Level level) {
        return switch (level.getStatus()) {
            case PLAYING -> new PlayingState();
            case WON -> new WonState();
            case LOST -> level.getLossReason() == LossReason.DEADLOCK ? new BlockedState() : new LostState();
        };
    }

    private void publish(GameEvent event) {
        for (GameListener listener : List.copyOf(listeners)) {   // a listener may subscribe while handling
            listener.onEvent(event);
        }
    }
}
