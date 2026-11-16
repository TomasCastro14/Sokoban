package model.game.state;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Game;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameUndoTest {

    private static final int LEVEL_COUNT = 5;

    private static Level corridor() {
        return TestLevels.level(
                "########",
                "#@     #",
                "#  $.  #",
                "########");
    }

    private static Level oneMoveLevel() {
        return TestLevels.level("######", "#@$. #", "######");
    }

    private static Level wrongStorageLevel() {
        return TestLevels.level("######", "#@3. #", "######");
    }

    @Test
    @DisplayName("En PLAYING el undo deshace y sigue en PLAYING")
    void undoWhilePlaying() {
        Game game = new Game(n -> corridor(), LEVEL_COUNT);
        game.start();
        game.move(Direction.RIGHT);

        game.undo();

        assertSame(PlayingState.INSTANCE, game.getState());
        assertEquals(new Position(1, 1), game.getCurrentLevel().getBoard().getPlayerPosition());
        assertEquals(1, game.getCurrentLevel().getStats().getUndos());
    }

    @Test
    @DisplayName("En WON no se puede deshacer")
    void noUndoWhenWon() {
        Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
        game.start();
        game.move(Direction.RIGHT);

        game.undo();

        assertSame(WonState.INSTANCE, game.getState());
        assertEquals(0, game.getCurrentLevel().getStats().getUndos());
        assertEquals(new Position(1, 2), game.getCurrentLevel().getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("En LOST no se puede deshacer")
    void noUndoWhenLost() {
        Game game = new Game(n -> wrongStorageLevel(), LEVEL_COUNT);
        game.start();
        game.move(Direction.RIGHT);

        game.undo();

        assertSame(LostState.INSTANCE, game.getState());
        assertEquals(0, game.getCurrentLevel().getStats().getUndos());
    }

    @Test
    @DisplayName("En el menu no se puede deshacer")
    void noUndoInMenu() {
        Game game = new Game(n -> corridor(), LEVEL_COUNT);

        game.undo();

        assertSame(MenuState.INSTANCE, game.getState());
    }

    @Test
    @DisplayName("Reiniciar vacia el historial: no queda nada para deshacer")
    void restartClearsTheHistory() {
        Game game = new Game(n -> corridor(), LEVEL_COUNT);
        game.start();
        game.move(Direction.RIGHT);
        game.restart();

        game.undo();

        assertEquals(0, game.getCurrentLevel().getStats().getUndos());
        assertEquals(new Position(1, 1), game.getCurrentLevel().getBoard().getPlayerPosition());
    }
}