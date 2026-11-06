package model.history;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.box.WeakBox;
import model.game.Level;
import model.tile.Tile;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UndoTest {

    private static Level corridor() {
        return TestLevels.level(
                "##############",
                "#@           #",
                "#  $.        #",
                "##############");
    }

    private static void moveRight(Level level, int times) {
        for (int i = 0; i < times; i++) {
            level.move(Direction.RIGHT);
        }
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
    @DisplayName("2. El undo restaura el contador de la WeakBox sobre la misma caja")
    void restoresWeakBoxCounter() {
        Level level = TestLevels.level(
                "#######",
                "#@3  :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        level.move(Direction.RIGHT);

        level.undo();

        assertSame(box, level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("3. El undo restaura puertas, la llave usada y la visibilidad, sin recrear casillas")
    void restoresDoorsTilesAndVisibility() {
        Level level = TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  ;   #",
                "########");
        Tile key = level.getBoard().getTile(new Position(1, 4));
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        level.undo();

        Door door = (Door) level.getBoard().getTile(new Position(2, 4));
        assertFalse(door.isOpen());
        assertSame(key, level.getBoard().getTile(new Position(1, 4)));
        assertFalse(((KeyTile) key).isUsed());
        assertFalse(level.getVisibility().isVisible(new Position(3, 1)));
    }

    @Test
    @DisplayName("4. Cada undo retrocede 5 movimientos")
    void eachUndoGoesBackFiveMoves() {
        Level level = corridor();
        moveRight(level, 7);

        level.undo();

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
        assertEquals(2, level.getStats().getMoves());
    }

    @Test
    @DisplayName("5. Con menos de 5 movimientos, el undo vuelve al inicio")
    void fewerThanFiveMovesGoesBackToTheStart() {
        Level level = corridor();
        moveRight(level, 3);

        assertTrue(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getMoves());
    }

    @Test
    @DisplayName("6. Solo hay 3 undos seguidos; el cuarto no hace nada")
    void onlyThreeConsecutiveUndos() {
        Level level = corridor();
        for (int i = 0; i < 10; i++) {
            level.move(Direction.RIGHT);
            level.move(Direction.LEFT);
        }

        assertTrue(level.undo());
        assertTrue(level.undo());
        assertTrue(level.undo());
        assertFalse(level.undo());

        assertEquals(5, level.getStats().getMoves());
        assertEquals(0, level.getRemainingUndos());
    }

    @Test
    @DisplayName("7. Un movimiento nuevo vuelve a habilitar los 3 undos")
    void newMoveResetsTheUndos() {
        Level level = corridor();
        moveRight(level, 2);
        level.undo();

        level.move(Direction.RIGHT);

        assertEquals(UndoHistory.MAX_CONSECUTIVE_UNDOS, level.getRemainingUndos());
    }

    @Test
    @DisplayName("8. Cada undo suma 1 a LevelStats.undos")
    void eachUndoIsCounted() {
        Level level = corridor();
        moveRight(level, 7);

        level.undo();
        assertEquals(1, level.getStats().getUndos());
        level.undo();
        assertEquals(2, level.getStats().getUndos());
    }

    @Test
    @DisplayName("9. Con el historial vacio, el undo no hace nada")
    void undoWithEmptyHistoryDoesNothing() {
        Level level = corridor();

        assertFalse(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getUndos());
        assertEquals(UndoHistory.MAX_CONSECUTIVE_UNDOS, level.getRemainingUndos());
    }

    @Test
    @DisplayName("10. Un movimiento bloqueado no se puede deshacer")
    void blockedMoveIsNotRecorded() {
        Level level = corridor();

        level.move(Direction.UP);

        assertFalse(level.undo());
    }
}