package model.game.state;

public final class PausedState extends AbstractGameState {

    public static final PausedState INSTANCE = new PausedState();

    private PausedState() {
    }

    @Override
    public GameState togglePause(GameContext game) {
        game.announceResume();
        return PlayingState.INSTANCE;
    }

    @Override
    public GameState restart(GameContext game) {
        game.announceResume();
        game.reloadLevel();
        return PlayingState.INSTANCE;
    }
}