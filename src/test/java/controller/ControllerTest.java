package controller;

import javafx.scene.input.KeyCode;
import model.board.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    @DisplayName("1. Cada flecha se traduce a su Direction")
    void arrowsMapToDirections() {
        assertEquals(Direction.UP, InputController.directionFor(KeyCode.UP));
        assertEquals(Direction.DOWN, InputController.directionFor(KeyCode.DOWN));
        assertEquals(Direction.LEFT, InputController.directionFor(KeyCode.LEFT));
        assertEquals(Direction.RIGHT, InputController.directionFor(KeyCode.RIGHT));
    }

    @Test
    @DisplayName("2. Una tecla que no es flecha no se traduce a ninguna Direction")
    void otherKeysHaveNoDirection() {
        assertNull(InputController.directionFor(KeyCode.SPACE));
        assertNull(InputController.directionFor(KeyCode.ESCAPE));
    }

    @Test
    @DisplayName("3. Una flecha le pide el movimiento a Game y nada mas")
    void arrowDelegatesMoveToGame() {
        RecordingGame game = new RecordingGame();

        assertTrue(new InputController(game).onKey(KeyCode.LEFT));

        assertEquals(List.of("move LEFT"), game.calls);
    }

    @Test
    @DisplayName("4. Una tecla que no es flecha no le pide nada a Game")
    void otherKeyDoesNotReachGame() {
        RecordingGame game = new RecordingGame();

        assertFalse(new InputController(game).onKey(KeyCode.ENTER));

        assertTrue(game.calls.isEmpty());
    }

    @Test
    @DisplayName("5. El boton Deshacer le pide undo a Game")
    void undoButtonDelegatesToGame() {
        RecordingGame game = new RecordingGame();

        new HudController(game).onUndoPressed();

        assertEquals(List.of("undo"), game.calls);
    }

    @Test
    @DisplayName("6. El boton Reiniciar le pide restart a Game")
    void restartButtonDelegatesToGame() {
        RecordingGame game = new RecordingGame();

        new HudController(game).onRestartPressed();

        assertEquals(List.of("restart"), game.calls);
    }

    @Test
    @DisplayName("7. Cada segundo del reloj le pide tick a Game")
    void clockSecondDelegatesTickToGame() {
        RecordingGame game = new RecordingGame();
        GameClock clock = new GameClock(game);

        clock.onSecond();
        clock.onSecond();

        assertEquals(List.of("tick", "tick"), game.calls);
    }
}
