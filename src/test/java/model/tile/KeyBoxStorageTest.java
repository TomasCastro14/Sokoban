package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.game.Level;
import model.game.loss.LossReason;
import model.tile.mechanism.Door;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Storing a key box after it opened its door, through the real key tile, in the same rule as EarlyKeyStorage. */
class KeyBoxStorageTest {

    /** The key box crosses key tile 'a' (opens door A) and then reaches its point ';'. */
    private static Level keyThroughTileToPoint() {
        return TestLevels.level(
                "########",
                "#@Ka;  #",
                "####A###",
                "#      #",
                "########");
    }

    private static StoragePoint pointAt(Level level, int row, int col) {
        return (StoragePoint) level.getBoard().getTile(new Position(row, col));
    }

    @Test
    @DisplayName("1. Al pasar por su baldosa la KeyBox abre la puerta, y despues se guarda sin derrota")
    void keyBoxStoredAfterOpeningItsDoorIsValid() {
        Level level = keyThroughTileToPoint();

        level.move(Direction.RIGHT);
        assertTrue(((Door) level.getBoard().getTile(new Position(2, 4))).isOpen());

        assertNotEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertTrue(pointAt(level, 1, 4).isFilled());
        assertEquals(StorageResult.STORED, pointAt(level, 1, 4).getStorageResult());
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("2. La KeyBox guardada suma a las cajas guardadas que usa la victoria")
    void storedKeyBoxCountsAsStoredBox() {
        Level level = keyThroughTileToPoint();

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(1, level.getStats().getStoredBoxes());
    }

    @Test
    @DisplayName("3. Los empujes que abren la puerta y guardan la KeyBox se cuentan como empujes normales")
    void storingPushesCountAsNormalPushes() {
        Level level = keyThroughTileToPoint();

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(2, level.getStats().getMoves());
        assertEquals(2, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. Con dos KeyBox, la que abrio su puerta se guarda bien y la otra da EARLY_KEY_STORAGE")
    void eachKeyBoxDependsOnItsOwnDoor() {
        Level level = TestLevels.level(
                "#########",
                "#@Ka;   #",
                "#  K;   #",
                "####A####",
                "#########");

        level.move(Direction.RIGHT); // first key box opens door A
        level.move(Direction.RIGHT); // and is stored
        assertNull(level.getLossReason());

        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT); // second key box never crossed a key tile

        assertEquals(LossReason.EARLY_KEY_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("5. Una KeyBox que abrio su puerta pero cae en un punto de otro color da WRONG_STORAGE")
    void openedKeyBoxOnOtherColorIsWrongStorage() {
        Level level = TestLevels.level(
                "########",
                "#@Ka.  #",
                "####A###",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertFalse(pointAt(level, 1, 4).isFilled());
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("6. Una NormalBox que pasa por la baldosa llave no abre la puerta y se guarda normal en su punto")
    void normalBoxIgnoresKeyTileAndIsStored() {
        Level level = TestLevels.level(
                "########",
                "#@$a.  #",
                "####A###",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertFalse(((Door) level.getBoard().getTile(new Position(2, 4))).isOpen());
        assertTrue(pointAt(level, 1, 4).isFilled());
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("7. Una WeakBox que pasa por la baldosa llave no abre la puerta y se guarda normal en su punto")
    void weakBoxIgnoresKeyTileAndIsStored() {
        Level level = TestLevels.level(
                "########",
                "#@3a:  #",
                "####A###",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertFalse(((Door) level.getBoard().getTile(new Position(2, 4))).isOpen());
        assertTrue(pointAt(level, 1, 4).isFilled());
        assertNull(level.getLossReason());
    }
}
