package model.game.state;

public class WonState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }
}
