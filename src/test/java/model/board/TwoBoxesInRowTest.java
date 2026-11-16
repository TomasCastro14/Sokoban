package model.board;

import factory.TestLevels;
import model.box.Box;
import model.box.WeakBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwoBoxesInRowTest {

    /** Two boxes in a row towards each direction, with free floor behind the second one. */
    private static Level crossOfBoxes() {
        return TestLevels.level(
                "#########",
                "#       #",
                "#   $   #",
                "#   $   #",
                "# $$@$$ #",
                "#   $   #",
                "#   $   #",
                "#       #",
                "#########");
    }

    private static Box boxAt(Level level, int row, int col) {
        return level.getBoard().getBoxAt(new Position(row, col));
    }

    @Test
    @DisplayName("1. Dos cajas en fila en cada una de las 4 direcciones dan BLOCKED")
    void twoBoxesBlockInEachDirection() {
        for (Direction direction : Direction.values()) {
            assertEquals(MoveResult.BLOCKED, crossOfBoxes().move(direction), direction.name());
        }
    }

    @Test
    @DisplayName("2. Despues del BLOCKED el jugador y las dos cajas siguen en el mismo lugar")
    void nothingMovesAfterBlocked() {
        Position player = new Position(4, 4);

        for (Direction direction : Direction.values()) {
            Level level = crossOfBoxes();
            Position first = player.move(direction);
            Position second = first.move(direction);
            Box firstBox = level.getBoard().getBoxAt(first);
            Box secondBox = level.getBoard().getBoxAt(second);

            level.move(direction);

            assertEquals(player, level.getBoard().getPlayerPosition(), direction.name());
            assertSame(firstBox, level.getBoard().getBoxAt(first), direction.name());
            assertSame(secondBox, level.getBoard().getBoxAt(second), direction.name());
            assertNull(level.getBoard().getBoxAt(second.move(direction)), direction.name());
        }
    }

    @Test
    @DisplayName("3. Despues del BLOCKED no suben los movimientos ni los empujes")
    void blockedCountsNothing() {
        Level level = crossOfBoxes();

        level.move(Direction.RIGHT);
        level.move(Direction.UP);

        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. WeakBox adelante y otra caja detras: BLOCKED y la WeakBox no pierde resistencia")
    void weakBoxInFrontKeepsResistance() {
        Level level = TestLevels.level(
                "#######",
                "#@3$  #",
                "#######");
        WeakBox weak = (WeakBox) boxAt(level, 1, 2);

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(3, weak.getRemainingPushes());
    }

    @Test
    @DisplayName("5. Otra caja adelante y WeakBox detras: BLOCKED y la WeakBox no pierde resistencia")
    void weakBoxBehindKeepsResistance() {
        Level level = TestLevels.level(
                "#######",
                "#@$3  #",
                "#######");
        WeakBox weak = (WeakBox) boxAt(level, 1, 3);

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(3, weak.getRemainingPushes());
    }

    @Test
    @DisplayName("6. KeyBox adelante y otra caja detras da BLOCKED")
    void keyBoxInFrontIsBlocked() {
        Level level = TestLevels.level(
                "#######",
                "#@K$  #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertNotNull(boxAt(level, 1, 2));
    }

    @Test
    @DisplayName("7. Otra caja adelante y KeyBox detras da BLOCKED")
    void keyBoxBehindIsBlocked() {
        Level level = TestLevels.level(
                "#######",
                "#@$K  #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertNotNull(boxAt(level, 1, 3));
    }

    @Test
    @DisplayName("8. Tres cajas en fila dan BLOCKED: no se empuja en cadena")
    void threeBoxesInRowAreBlocked() {
        Level level = TestLevels.level(
                "########",
                "#@$$$  #",
                "########");
        Box first = boxAt(level, 1, 2);
        Box second = boxAt(level, 1, 3);
        Box third = boxAt(level, 1, 4);

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertSame(first, boxAt(level, 1, 2));
        assertSame(second, boxAt(level, 1, 3));
        assertSame(third, boxAt(level, 1, 4));
        assertNull(boxAt(level, 1, 5));
    }

    @Test
    @DisplayName("9. Aunque haya lugar libre detras de la segunda caja, dos cajas no se empujan a la vez")
    void freeCellBehindSecondBoxStillBlocks() {
        Level level = TestLevels.level(
                "########",
                "#@$$   #",
                "########");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertNull(boxAt(level, 1, 4));
    }

    @Test
    @DisplayName("10. Una caja guardada en su punto es puente (piso): se puede empujar otra caja encima")
    void storedBoxDoesNotBlockAnotherBox() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#  $  #",
                "#     #",
                "#######");
        Box second = boxAt(level, 2, 3);
        level.move(Direction.RIGHT); // stores the first box at (1,3)
        level.move(Direction.DOWN);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);

        assertNotEquals(MoveResult.BLOCKED, level.move(Direction.UP));

        assertSame(second, boxAt(level, 1, 3));
    }
}
