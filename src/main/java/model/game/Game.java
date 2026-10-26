package model.game;

import model.board.Direction;
import model.game.state.GameState;

import java.util.function.IntFunction;

public class Game {
    private final IntFunction<Level> levelFactory;
    private final int levelCount;

    /**
     * @param levelFactory builds a fresh level from its number (1-based)
     * @param levelCount   number of levels in the game
     */
    public Game(IntFunction<Level> levelFactory, int levelCount) {
        this.levelFactory = levelFactory;
        this.levelCount = levelCount;
    }

    public GameState getState() {
        throw new UnsupportedOperationException("TODO");
    }

    public Level getCurrentLevel() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getCurrentLevelNumber() {
        throw new UnsupportedOperationException("TODO");
    }

    public void start() {
        throw new UnsupportedOperationException("TODO");
    }

    public void move(Direction direction) {
        throw new UnsupportedOperationException("TODO");
    }

    public void undo() {
        throw new UnsupportedOperationException("TODO");
    }

    public void restart() {
        throw new UnsupportedOperationException("TODO");
    }

    public void nextLevel() {
        throw new UnsupportedOperationException("TODO");
    }

    /** One second of play time has passed. */
    public void tick() {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isInputBlocked() {
        throw new UnsupportedOperationException("TODO");
    }
}
