package model.tile.mechanism;

import factory.TestLevels;
import model.board.Cell;
import model.board.Direction;
import model.board.Position;
import model.box.KeyBox;
import model.game.Level;
import model.game.event.EventRecorder;
import model.tile.Tile;
import model.tile.TileMemento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyTileUsageTest {

    private static final Position TILE = new Position(1, 4);
    private static final Position DOOR = new Position(4, 4);

    private static Level levelWithKeyBoxOnItsWay() {
        return TestLevels.level(
                "#########",
                "#@K a   #",
                "#   K   #",
                "#       #",
                "####A####",
                "#       #",
                "#########");
    }

    private static void play(Level level, Direction... moves) {
        for (Direction move : moves) {
            level.move(move);
        }
    }

    private static Door door(Level level) {
        return (Door) level.getBoard().getTile(DOOR);
    }

    @Test
    @DisplayName("La celda mantiene la misma instancia de KeyTile antes y despues de usarla")
    void cellKeepsTheSameKeyTile() {
        Level level = levelWithKeyBoxOnItsWay();
        Cell cell = level.getBoard().cellAt(TILE);
        Tile before = cell.getTile();

        play(level, Direction.RIGHT, Direction.RIGHT);

        assertSame(cell, level.getBoard().cellAt(TILE));
        assertSame(before, cell.getTile());
        assertInstanceOf(KeyTile.class, cell.getTile());
    }

    @Test
    @DisplayName("La llave sin usar abre las puertas de su grupo")
    void unusedKeyOpensTheDoor() {
        Level level = levelWithKeyBoxOnItsWay();

        play(level, Direction.RIGHT, Direction.RIGHT);

        assertTrue(door(level).isOpen());
    }

    @Test
    @DisplayName("La KeyBox queda en la celda de la llave")
    void keyBoxStaysInTheCell() {
        Level level = levelWithKeyBoxOnItsWay();

        play(level, Direction.RIGHT, Direction.RIGHT);

        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(TILE));
    }

    @Test
    @DisplayName("Una KeyBox que sale y vuelve a entrar no abre dos veces ni avisa dos veces")
    void reenteringDoesNotOpenAgain() {
        Level level = levelWithKeyBoxOnItsWay();
        EventRecorder events = EventRecorder.attachTo(level);

        play(level, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        door(level).close();
        play(level, Direction.DOWN, Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.LEFT);

        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(TILE));
        assertFalse(door(level).isOpen(), "la llave usada no vuelve a abrir la puerta");
        assertEquals(1, events.count("doorOpened"));
    }

    @Test
    @DisplayName("Otra KeyBox que entra en la celda no abre nada")
    void anotherKeyBoxOpensNothing() {
        Level level = levelWithKeyBoxOnItsWay();
        EventRecorder events = EventRecorder.attachTo(level);

        play(level, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        door(level).close();
        play(level, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT, Direction.UP);

        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(TILE));
        assertFalse(door(level).isOpen());
        assertEquals(1, events.count("doorOpened"));
    }

    @Test
    @DisplayName("Una NormalBox sobre la llave no la usa")
    void normalBoxDoesNotUseTheKey() {
        Level level = TestLevels.level(
                "########",
                "#@$a   #",
                "####A###",
                "#      #",
                "########");
        KeyTile key = (KeyTile) level.getBoard().getTile(new Position(1, 3));

        level.move(Direction.RIGHT);

        assertSame(key, level.getBoard().getTile(new Position(1, 3)));
        assertFalse(key.isUsed());
    }

    @Test
    @DisplayName("El memento restaura el estado sobre la misma casilla del tablero")
    void mementoRestoresTheSameTile() {
        Level level = levelWithKeyBoxOnItsWay();
        Tile key = level.getBoard().getTile(TILE);
        TileMemento beforeUse = key.save();
        play(level, Direction.RIGHT, Direction.RIGHT);

        beforeUse.restore();

        assertSame(key, level.getBoard().getTile(TILE));
        assertFalse(((KeyTile) key).isUsed());
    }
}