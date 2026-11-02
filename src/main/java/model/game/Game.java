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

public class Game {
    private final IntFunction<Level> levelFactory;
    private final int levelCount;
    private final List<GameListener> listeners = new ArrayList<>();
    private GameState state = new MenuState();
    private Level level;
    private int levelNumber;

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

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    public GameState getState() {
        return state;
    }

    public Level getCurrentLevel() {
        if (level == null) {
            throw new IllegalStateException("The game has not started: call start() first");
        }
        return level;
    }

    public int getCurrentLevelNumber() {
        return levelNumber;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void start() {
        start(1);
    }

    public void start(int number) {
        if (number < 1 || number > levelCount) {
            throw new IllegalArgumentException("Level " + number + " is out of range 1.." + levelCount);
        }
        load(number);
        publish(new GameEvent(EventType.LEVEL_STARTED));
    }

    public void move(Direction direction) {
        if (state.acceptsInput()) {
            level.move(direction);
        }
    }

    public void undo() {
        if (state.allowsUndo() && level.undo()) {
            state = stateFor(level);
            publish(new GameEvent(EventType.BOARD_RESTORED));
        }
    }

    public void restart() {
        if (state.allowsRestart()) {
            load(levelNumber);
            publish(new GameEvent(EventType.LEVEL_STARTED));
        }
    }

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

    private static GameState stateFor(Level level) {
        return switch (level.getStatus()) {
            case PLAYING -> new PlayingState();
            case WON -> new WonState();
            case LOST -> level.getLossReason() == LossReason.DEADLOCK ? new BlockedState() : new LostState();
        };
    }

    private void publish(GameEvent event) {
        for (GameListener listener : List.copyOf(listeners)) {
            listener.onEvent(event);
        }
    }
}