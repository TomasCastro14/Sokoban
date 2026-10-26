package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.Box;
import model.box.WeakBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/** A pushed box that enters ice keeps sliding; the player only advances one cell. */
class BoxSlideTest {

    @Test
    @DisplayName("La caja se desliza por un camino largo y queda en la ultima celda de hielo")
    void boxEndsOnLastIceCell() {
        Level level = TestLevels.level(
                "#######",
                "#@$~~~#",
                "# .   #",
                "#######");

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 5)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("El jugador avanza solo 1 celda mientras la caja se desliza")
    void playerAdvancesOnlyOne() {
        Level level = TestLevels.level(
                "#######",
                "#@$~~~#",
                "# .   #",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes()); // one push per action, however far the box slides
    }

    @Test
    @DisplayName("Una WeakBox que se desliza descuenta un solo empuje")
    void weakBoxLosesOnlyOnePush() {
        Level level = TestLevels.level(
                "#########",
                "#@3~~~ :#",
                "#########");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        level.move(Direction.RIGHT);

        assertSame(box, level.getBoard().getBoxAt(new Position(1, 6)));
        assertEquals(2, box.getRemainingPushes());
    }

    @Test
    @DisplayName("Una caja que se desliza hasta un punto de su color se guarda alli, y recien alli")
    void slidingBoxIsStoredOnlyAtTheEnd() {
        Level level = TestLevels.level(
                "########",
                "#@$~~~.#",
                "########");
        Box box = level.getBoard().getBoxAt(new Position(1, 2));

        level.move(Direction.RIGHT);

        StoragePoint point = (StoragePoint) level.getBoard().getTile(new Position(1, 6));
        assertTrue(point.isFilled());
        assertSame(box, point.getStoredBox());
        assertEquals(StorageResult.STORED, point.getStorageResult());
        assertEquals(1, level.getStats().getStoredBoxes());
        assertTrue(level.isWon());
    }

    /** Four boxes around the player, each followed by 2 ice cells and the border wall. */
    @ParameterizedTest(name = "Empujando hacia {0}: la caja termina en ({1},{2}) y el jugador en ({3},{4})")
    @CsvSource({"UP, 1, 4, 3, 4", "DOWN, 7, 4, 5, 4", "LEFT, 4, 1, 4, 3", "RIGHT, 4, 7, 4, 5"})
    void boxSlidesInEveryDirection(Direction direction, int boxRow, int boxCol, int playerRow, int playerCol) {
        Level level = TestLevels.level(
                "#########",
                "#.  ~  .#",
                "#   ~   #",
                "#   $   #",
                "#~~$@$~~#",
                "#   $   #",
                "#   ~   #",
                "#.  ~  .#",
                "#########");

        level.move(direction);

        assertNotNull(level.getBoard().getBoxAt(new Position(boxRow, boxCol)));
        assertEquals(new Position(playerRow, playerCol), level.getBoard().getPlayerPosition());
    }
}
