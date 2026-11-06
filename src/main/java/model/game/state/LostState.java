package model.game.state;

public final class LostState implements GameState {

    public static final LostState INSTANCE = new LostState();

    private LostState() {
    }

    @Override
    public GameState restart(GameContext game) {
        game.reloadLevel();
        return PlayingState.INSTANCE;
    }
}