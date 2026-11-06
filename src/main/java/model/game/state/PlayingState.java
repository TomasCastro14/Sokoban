package model.game.state;

import model.board.Direction;
import model.game.LevelStats;
import model.game.loss.LossReason;

public final class PlayingState extends AbstractGameState {

    public static final PlayingState INSTANCE = new PlayingState();

    private PlayingState() {
    }

    @Override
    public GameState move(GameContext game, Direction direction) {
        return game.playMove(direction);
    }

    @Override
    public GameState undo(GameContext game) {
        game.undoLastMove();
        return this;
    }

    @Override
    public GameState restart(GameContext game) {
        game.reloadLevel();
        return this;
    }

    @Override
    public GameState togglePause(GameContext game) {
        game.announcePause();
        return PausedState.INSTANCE;
    }

    @Override
    public GameState tick(GameContext game) {
        return game.elapseSecond();
    }

    @Override
    public GameState onLevelWon(LevelStats stats) {
        return WonState.INSTANCE;
    }

    @Override
    public GameState onLevelLost(GameContext game, LossReason reason) {
        return game.canRecoverFrom(reason) ? BlockedState.INSTANCE : LostState.INSTANCE;
    }
}