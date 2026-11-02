package controller;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.board.Direction;
import model.game.Game;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class InputController {
    private static final Map<KeyCode, Direction> ARROWS = Collections.unmodifiableMap(new EnumMap<>(Map.of(
            KeyCode.UP, Direction.UP,
            KeyCode.DOWN, Direction.DOWN,
            KeyCode.LEFT, Direction.LEFT,
            KeyCode.RIGHT, Direction.RIGHT)));

    private final Game game;

    public InputController(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    public Runnable bindKeys(Scene scene) {
        EventHandler<KeyEvent> handler = event -> {
            if (onKey(event.getCode())) {
                event.consume();
            }
        };
        scene.addEventHandler(KeyEvent.KEY_PRESSED, handler);
        return () -> scene.removeEventHandler(KeyEvent.KEY_PRESSED, handler);
    }

    boolean onKey(KeyCode key) {
        Optional<Direction> direction = directionFor(key);
        direction.ifPresent(game::move);
        return direction.isPresent();
    }

    static Optional<Direction> directionFor(KeyCode key) {
        return Optional.ofNullable(ARROWS.get(key));
    }
}