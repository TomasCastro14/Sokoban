package model.game.state;

public class PlayingState implements GameState {

    @Override
    public boolean acceptsInput() {
        return true;
    }
}
