package model.game.state;

public class LostState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }
}
