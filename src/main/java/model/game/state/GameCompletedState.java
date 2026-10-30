package model.game.state;

public class GameCompletedState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }

    @Override
    public boolean allowsUndo() {
        return false;
    }

    @Override
    public boolean allowsRestart() {
        return false;
    }

    @Override
    public boolean allowsNextLevel() {
        return false;
    }
}
