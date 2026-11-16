package controller;

import model.board.Direction;
import model.game.Game;

import java.util.ArrayList;
import java.util.List;

/** Test double: records what the controllers ask the game, without running any game logic. */
class RecordingGame extends Game {
    final List<String> calls = new ArrayList<>();

    RecordingGame() {
        super(n -> null, 1);
    }

    @Override
    public void move(Direction direction) {
        calls.add("move " + direction);
    }

    @Override
    public void undo() {
        calls.add("undo");
    }

    @Override
    public void restart() {
        calls.add("restart");
    }

    @Override
    public void tick() {
        calls.add("tick");
    }
}
