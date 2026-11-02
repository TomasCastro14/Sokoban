package controller;

import model.game.Game;

import java.util.Objects;

public final class HudController {
    private final Game game;

    public HudController(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    public void onUndoPressed() {
        game.undo();
    }

    public void onRestartPressed() {
        game.restart();
    }
}