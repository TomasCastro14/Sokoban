package model.game.state;

/** Deadlock: la partida se frena, pero un undo la recupera. También se puede reiniciar. */
public final class BlockedState extends AbstractGameState {

    public static final BlockedState INSTANCE = new BlockedState();

    private BlockedState() {
    }

    @Override
    public GameState undo(GameContext game) {
        return game.undoLastMove() ? PlayingState.INSTANCE : this;
    }

    @Override
    public GameState restart(GameContext game) {
        game.reloadLevel();
        return PlayingState.INSTANCE;
    }
}