package controller;

import javafx.event.EventHandler;
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

    /**
     * Adds a handler instead of replacing the scene's own one (the window uses it for ESC).
     * Returns the way to take it off again, so a screen that is hidden stops reacting to keys.
     */
    public Runnable bindKeys(Scene scene) {
        EventHandler<KeyEvent> handler = event -> {
            if (onKey(event.getCode())) {
                event.consume();
            }
        };
        scene.addEventHandler(KeyEvent.KEY_PRESSED, handler);
        return () -> scene.removeEventHandler(KeyEvent.KEY_PRESSED, handler);
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