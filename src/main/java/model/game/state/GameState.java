package model.game.state;

import model.board.Direction;
import model.game.LevelStats;
import model.game.loss.LossReason;

public interface GameState {

    default GameState move(GameContext game, Direction direction) {
        return this;
    }

    default GameState undo(GameContext game) {
        return this;
    }

    default GameState restart(GameContext game) {
        return this;
    }

    default GameState nextLevel(GameContext game) {
        return this;
    }

    default GameState tick(GameContext game) {
        return this;
    }

    default GameState onLevelWon(LevelStats stats) {
        return this;
    }

    default GameState onLevelLost(LossReason reason) {
        return this;
    }
}