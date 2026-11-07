package model.game.state;

import model.board.Direction;
import model.game.LevelStats;
import model.game.loss.LossReason;

/** State (GoF): each state answers every input. AbstractGameState gives the "ignore it" answer. */
public interface GameState {

    GameState move(GameContext game, Direction direction);

    GameState undo(GameContext game);

    GameState restart(GameContext game);

    GameState nextLevel(GameContext game);

    GameState togglePause(GameContext game);

    GameState tick(GameContext game);

    GameState onLevelWon(LevelStats stats);

    GameState onLevelLost(GameContext game, LossReason reason);
}
