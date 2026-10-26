package model.tile.mechanism;

import factory.TestLevels;
import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.WeakBox;
import model.game.Level;
import model.game.event.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Only a key box activates a key tile; for any other box the tile is plain floor. */
class KeyTileOnlyKeyBoxTest {

    private static Door doorAt(Board board, int row, int col) {
        return assertInstanceOf(Door.class, board.getTile(new Position(row, col)));
    }

    private static KeyTile keyTileAt(Board board, int row, int col) {
        return assertInstanceOf(KeyTile.class, board.getTile(new Position(row, col)));
    }

    @Test
    @DisplayName("Una NormalBox sobre la baldosa a no abre las puertas A")
    void normalBoxOnKeyTileOpensNothing() {
        Level level = TestLevels.level(
                "#########",
                "#   A   #",
                "#@$a   .#",
                "#########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(2, 3)));
        assertFalse(doorAt(level.getBoard(), 1, 4).isOpen());
        assertFalse(keyTileAt(level.getBoard(), 2, 3).isUsed());
    }

    @Test
    @DisplayName("Una WeakBox sobre la baldosa a no abre las puertas A y descuenta como en cualquier empuje")
    void weakBoxOnKeyTileOpensNothing() {
        Level level = TestLevels.level(
                "#########",
                "#   A   #",
                "#@3a   :#",
                "#########");

        level.move(Direction.RIGHT);

        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(2, 3));
        assertEquals(2, box.getRemainingPushes());
        assertEquals(1, level.getStats().getPushes());
        assertFalse(doorAt(level.getBoard(), 1, 4).isOpen());
        assertFalse(keyTileAt(level.getBoard(), 2, 3).isUsed());
    }

    @Test
    @DisplayName("Una NormalBox que pasa por la baldosa y sale la deja intacta")
    void normalBoxLeavesKeyTileIntact() {
        Level level = TestLevels.level(
                "#########",
                "#   A   #",
                "#@$a   .#",
                "#########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertNull(level.getBoard().getBoxAt(new Position(2, 3)));
        assertNotNull(level.getBoard().getBoxAt(new Position(2, 4)));
        assertFalse(keyTileAt(level.getBoard(), 2, 3).isUsed());
        assertFalse(doorAt(level.getBoard(), 1, 4).isOpen());
    }

    @Test
    @DisplayName("Despues de que una NormalBox sale, una KeyBox que entra a la baldosa abre las puertas")
    void keyBoxStillOpensAfterNormalBoxPassed() {
        Level level = TestLevels.level(
                "#########",
                "#   A   #",
                "#@$a   .#",
                "#  K    #",
                "#      ;#",
                "#########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);
        assertFalse(doorAt(level.getBoard(), 1, 4).isOpen());

        level.move(Direction.UP);

        assertTrue(keyTileAt(level.getBoard(), 2, 3).isUsed());
        assertTrue(doorAt(level.getBoard(), 1, 4).isOpen());
    }

    @Test
    @DisplayName("Una NormalBox o una WeakBox sobre la baldosa no disparan el aviso de apertura")
    void noOpeningNoticeForOtherBoxes() {
        assertEquals(0, openingNoticesAfterPushingOnKeyTile('$', '.'));
        assertEquals(0, openingNoticesAfterPushingOnKeyTile('3', ':'));
    }

    private static int openingNoticesAfterPushingOnKeyTile(char box, char storage) {
        Level level = TestLevels.level(
                "#########",
                "#   A   #",
                "#@" + box + "a   " + storage + "#",
                "#########");
        int[] notices = {0};
        level.addListener(event -> {
            if (event.getType() == EventType.DOOR_OPENED) {
                notices[0]++;
            }
        });

        level.move(Direction.RIGHT);

        return notices[0];
    }
}
