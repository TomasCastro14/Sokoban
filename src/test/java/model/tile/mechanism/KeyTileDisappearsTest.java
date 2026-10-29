package model.tile.mechanism;

import factory.TestLevels;
import model.board.Direction;
import model.box.KeyBox;
import model.game.Level;
import model.game.event.EventType;
import model.board.Position;
import model.tile.Floor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** A key tile is consumed by the key box that activates it: the terrain becomes floor, the box stays. */
class KeyTileDisappearsTest {

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
    @DisplayName("Despues de la KeyBox, la celda ya no tiene una KeyTile y su terreno es Floor")
    void tileBecomesFloor() {
        Level level = levelWithKeyBoxOnItsWay();

        play(level, Direction.RIGHT, Direction.RIGHT);

        assertInstanceOf(Floor.class, level.getBoard().getTile(TILE));
        assertFalse(level.getBoard().getTile(TILE) instanceof KeyTile);
    }

    @Test
    @DisplayName("La KeyBox sigue en la celda despues de que la baldosa desaparece")
    void keyBoxStaysInTheCell() {
        Level level = levelWithKeyBoxOnItsWay();

        play(level, Direction.RIGHT, Direction.RIGHT);

        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(TILE));
    }

    @Test
    @DisplayName("Una KeyBox que sale y vuelve a entrar no abre dos veces ni avisa dos veces")
    void reenteringDoesNotOpenAgain() {
        Level level = levelWithKeyBoxOnItsWay();
        int[] notices = {0};
        level.addListener(event -> {
            if (event.getType() == EventType.DOOR_OPENED) {
                notices[0]++;
            }
        });

        play(level, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        door(level).close();
        play(level, Direction.DOWN, Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.LEFT);

        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(TILE));
        assertFalse(door(level).isOpen(), "the consumed tile does not open the door again");
        assertEquals(1, notices[0]);
    }

    @Test
    @DisplayName("Otra KeyBox que entra en la celda no abre nada")
    void anotherKeyBoxOpensNothing() {
        Level level = levelWithKeyBoxOnItsWay();
        int[] notices = {0};
        level.addListener(event -> {
            if (event.getType() == EventType.DOOR_OPENED) {
                notices[0]++;
            }
        });

        play(level, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        door(level).close();
        play(level, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT, Direction.UP);

        assertInstanceOf(KeyBox.class, level.getBoard().getBoxAt(TILE));
        assertFalse(door(level).isOpen());
        assertEquals(1, notices[0]);
    }

    @Test
    @DisplayName("Una NormalBox sobre la baldosa no la consume")
    void normalBoxDoesNotConsumeTheTile() {
        Level level = TestLevels.level(
                "########",
                "#@$a   #",
                "####A###",
                "#      #",
                "########");

        level.move(Direction.RIGHT);

        assertInstanceOf(KeyTile.class, level.getBoard().getTile(new Position(1, 3)));
    }
}
