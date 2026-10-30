package model.game.state;

public interface GameState {

    boolean acceptsInput();
    boolean allowsUndo();
    boolean allowsRestart();
    boolean allowsNextLevel();
}
