package model.game.state;

public class GameCompletedState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }
}
