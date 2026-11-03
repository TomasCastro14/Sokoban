package model.history;

import factory.TestLevels;
import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.KeyBox;
import model.box.WeakBox;
import model.game.Level;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelMementoTest {

    private static final Position KEY = new Position(1, 4);
    private static final Position DOOR = new Position(2, 4);

    private static Level keyLevel() {
        return TestLevels.level(
                "#########",
                "#@K a   #",
                "####A####",
                "#  ;    #",
                "#########");
    }

    private static KeyTile key(Level level) {
        return (KeyTile) level.getBoard().getTile(KEY);
    }

    private static Door door(Level level) {
        return (Door) level.getBoard().getTile(DOOR);
    }

    @Test
    @DisplayName("Restaurar vuelve el jugador, las cajas y las estadisticas al momento del snapshot")
    void restoresPlayerBoxesAndStats() {
        Level level = keyLevel();
        LevelMemento start = level.save();
        level.move(Direction.RIGHT);

        start.restore();

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("Restaurar un snapshot previo a usar la llave la deja sin usar y cierra la puerta")
    void restoresAnUnusedKey() {
        Level level = keyLevel();
        level.move(Direction.RIGHT);
        LevelMemento beforeKey = level.save();
        level.move(Direction.RIGHT);

        beforeKey.restore();

        assertFalse(key(level).isUsed());
        assertFalse(door(level).isOpen());
        assertFalse(((KeyBox) level.getBoard().getBoxAt(new Position(1, 3))).hasOpenedDoor());
    }

    @Test
    @DisplayName("Restaurar un snapshot con la llave usada la deja usada y la puerta abierta")
    void restoresAUsedKey() {
        Level level = keyLevel();
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        LevelMemento afterKey = level.save();
        level.move(Direction.RIGHT);

        afterKey.restore();

        assertTrue(key(level).isUsed());
        assertTrue(door(level).isOpen());
        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(KEY));
    }

    @Test
    @DisplayName("Restaurar no recrea casillas ni celdas")
    void keepsTheSameCellsAndTiles() {
        Level level = keyLevel();
        Board board = level.getBoard();
        Object cell = board.cellAt(KEY);
        Object tile = board.getTile(KEY);
        LevelMemento start = level.save();
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        start.restore();

        assertSame(cell, board.cellAt(KEY));
        assertSame(tile, board.getTile(KEY));
    }

    @Test
    @DisplayName("El snapshot no cambia aunque el nivel siga moviendose")
    void snapshotIsImmutable() {
        Level level = keyLevel();
        LevelMemento start = level.save();
        level.move(Direction.RIGHT);
        start.restore();
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        start.restore();

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertFalse(key(level).isUsed());
    }

    @Test
    @DisplayName("BoxSnapshot devuelve la misma caja a su posicion con su contador")
    void boxSnapshotRestoresTheSameBox() {
        Level level = TestLevels.level(
                "#######",
                "#@3  :#",
                "#######");
        Board board = level.getBoard();
        WeakBox box = (WeakBox) board.getBoxAt(new Position(1, 2));
        BoxSnapshot snapshot = new BoxSnapshot(board, box, new Position(1, 2));
        level.move(Direction.RIGHT);
        board.cellAt(new Position(1, 3)).remove();

        snapshot.restore();

        assertSame(box, board.getBoxAt(new Position(1, 2)));
        assertEquals(3, box.getRemainingPushes());
    }
}