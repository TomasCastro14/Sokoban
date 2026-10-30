package model.game.state;

public class PlayingState implements GameState {

    @Override
    public boolean acceptsInput() {
        return true;
    }

    @Override
    public boolean allowsUndo() {
        return false;
    }

    @Override
    public boolean allowsRestart() {
        return true;
    }

    @Override
    public boolean allowsNextLevel() {
        return false;
    }
}
