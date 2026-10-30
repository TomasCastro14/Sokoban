package model.game.state;

public class WonState implements GameState {

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
        return true;
    }

    @Override
    public boolean allowsNextLevel() {
        return true;
    }
}
