package model.game.state;

public class BlockedState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }

    @Override
    public boolean allowsUndo() {
        return true;
    }

    @Override
    public boolean allowsRestart() {
        return true;
    }

    @Override
    public boolean allowsNextLevel() { return false; }
}
