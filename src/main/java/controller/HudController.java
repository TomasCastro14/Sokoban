package controller;

import model.game.Game;

/** MVC controller for the HUD buttons. Whether undo or restart is allowed is up to the game state. */
public class HudController {
    private final Game game;

    public HudController(Game game) {
        this.game = game;
    }

    public void onUndoPressed() {
        game.undo();
    }

    public void onRestartPressed() {
        game.restart();
    }
}
