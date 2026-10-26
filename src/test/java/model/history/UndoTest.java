package model.history;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.box.WeakBox;
import model.game.Level;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UndoTest {

    private static Level corridor() {
        return TestLevels.level(
                "########",
                "#@     #",
                "#  $.  #",
                "########");
    }

    @Test
    @DisplayName("1. El undo restaura la posicion del jugador y de las cajas")
    void restoresPlayerAndBoxes() {
        Level level = TestLevels.level(
                "#######",
                "#@$  .#",
                "#######");
        level.move(Direction.RIGHT);

        assertTrue(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("2. El undo restaura el contador de la WeakBox")
    void restoresWeakBoxCounter() {
        Level level = TestLevels.level(
                "#######",
                "#@3  :#",
                "#######");
        level.move(Direction.RIGHT);

        level.undo();

        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("3. El undo restaura puertas, baldosas y visibilidad")
    void restoresDoorsTilesAndVisibility() {
        Level level = TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  ;   #",
                "########");
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT); // opens door A, key tile is consumed

        level.undo();

        Door door = (Door) level.getBoard().getTile(new Position(2, 4));
        assertFalse(door.isOpen());
        assertInstanceOf(KeyTile.class, level.getBoard().getTile(new Position(1, 4)));
        assertFalse(level.getVisibility().isVisible(new Position(3, 1)));
    }

    @Test
    @DisplayName("4. Solo hay 3 undos por nivel; el cuarto no hace nada")
    void onlyThreeUndos() {
        Level level = corridor();
        for (int i = 0; i < 4; i++) {
            level.move(Direction.RIGHT); // (1,1) -> (1,5)
        }

        assertTrue(level.undo());
        assertTrue(level.undo());
        assertTrue(level.undo());
        assertFalse(level.undo());

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getRemainingUndos());
    }

    @Test
    @DisplayName("5. Cada undo suma 1 a LevelStats.undos")
    void eachUndoIsCounted() {
        Level level = corridor();
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        level.undo();
        assertEquals(1, level.getStats().getUndos());
        level.undo();
        assertEquals(2, level.getStats().getUndos());
    }

    @Test
    @DisplayName("6. Con el historial vacio, el undo no hace nada")
    void undoWithEmptyHistoryDoesNothing() {
        Level level = corridor();

        assertFalse(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getUndos());
        assertEquals(3, level.getRemainingUndos());
    }
}
