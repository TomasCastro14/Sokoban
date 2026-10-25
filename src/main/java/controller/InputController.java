package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.board.Direction;
import model.game.Game;

import java.util.EnumMap;
import java.util.Map;

/**
 * MVC controller: translates the arrow keys into a Direction and hands it to the game.
 * It never decides whether a move is allowed; the game state does.
 */
public class InputController {
    private static final Map<KeyCode, Direction> ARROWS = new EnumMap<>(Map.of(
            KeyCode.UP, Direction.UP,
            KeyCode.DOWN, Direction.DOWN,
            KeyCode.LEFT, Direction.LEFT,
            KeyCode.RIGHT, Direction.RIGHT));

    private final Game game;

    public InputController(Game game) {
        this.game = game;
    }

    /** Adds a handler instead of replacing the scene's own one (the window uses it for ESC). */
    public void bindKeys(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (onKey(event.getCode())) {
                event.consume();
            }
        });
    }

    /** Returns true when the key was an arrow and reached the game. */
    boolean onKey(KeyCode key) {
        Direction direction = directionFor(key);
        if (direction == null) {
            return false;
        }
        onArrow(direction);
        return true;
    }

    /** Returns the direction of an arrow key, or null for any other key. */
    static Direction directionFor(KeyCode key) {
        return ARROWS.get(key);
    }

    private void onArrow(Direction direction) {
        game.move(direction);
    }
}
