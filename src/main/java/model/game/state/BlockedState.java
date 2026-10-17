package model.game.state;

public class BlockedState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }
}
