package model.game.state;

public final class WonState implements GameState {

    public static final WonState INSTANCE = new WonState();

    private WonState() {
    }

    @Override
    public GameState restart(GameContext game) {
        game.reloadLevel();
        return PlayingState.INSTANCE;
    }

    @Override
    public GameState nextLevel(GameContext game) {
        if (game.isLastLevel()) {
            game.completeGame();
            return GameCompletedState.INSTANCE;
        }
        game.loadNextLevel();
        return PlayingState.INSTANCE;
    }
}