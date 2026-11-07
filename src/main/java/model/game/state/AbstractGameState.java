package model.game.state;

import model.board.Direction;
import model.game.LevelStats;
import model.game.loss.LossReason;

/**
 * Every input is ignored unless a state overrides it, so each state only writes the transitions it has.
 * It replaces the default methods of GameState, which the course does not allow.
 */
abstract class AbstractGameState implements GameState {

    @Override
    public GameState move(GameContext game, Direction direction) {
        return this;
    }

    @Override
    public GameState undo(GameContext game) {
        return this;
    }

    @Override
    public GameState restart(GameContext game) {
        return this;
    }

    @Override
    public GameState nextLevel(GameContext game) {
        return this;
    }

    @Override
    public GameState togglePause(GameContext game) {
        return this;
    }

    @Override
    public GameState tick(GameContext game) {
        return this;
    }

    @Override
    public GameState onLevelWon(LevelStats stats) {
        return this;
    }

    @Override
    public GameState onLevelLost(GameContext game, LossReason reason) {
        return this;
    }
}
