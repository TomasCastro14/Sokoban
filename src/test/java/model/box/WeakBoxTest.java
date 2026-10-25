package model.box;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeakBoxTest {

    @Test
    @DisplayName("1. WeakBox(1) aguanta 1 empuje: el primero da OK y el segundo da BROKEN y la caja desaparece")
    void weakBoxOfOneBreaksOnSecondPush() {
        Level level = TestLevels.level(
                "#######",
                "#@1  :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertEquals(0, box.getRemainingPushes());

        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)), "the board removes the broken box");
        assertNull(level.getBoard().getBoxAt(new Position(1, 4)), "the broken box does not move on");
    }

    @Test
    @DisplayName("2. WeakBox(3): los empujes intermedios dan OK y el contador baja de a 1")
    void weakBoxOfThreeCountsDownOneByOne() {
        Level level = TestLevels.level(
                "#########",
                "#@3    :#",
                "#########");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        for (int expected = 2; expected >= 0; expected--) {
            assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
            assertEquals(expected, box.getRemainingPushes());
        }
        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));
    }

    @Test
    @DisplayName("3. Un empuje bloqueado da BLOCKED y no descuenta")
    void blockedPushDoesNotCountDown() {
        Level level = TestLevels.level(
                "#######",
                "#@2# :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(2, box.getRemainingPushes());
        assertSame(box, level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. Caminar al lado de la WeakBox sin empujarla no descuenta")
    void walkingNextToBoxDoesNotCountDown() {
        Level level = TestLevels.level(
                "#######",
                "#@    #",
                "#  3 :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(2, 3));

        for (int i = 0; i < 4; i++) {
            level.move(Direction.RIGHT);
        }

        assertEquals(3, box.getRemainingPushes());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("5. Caminar 1 celda al lado de una WeakBox(3) no descuenta")
    void walkingOneCellNextToBoxDoesNotCountDown() {
        Level level = TestLevels.level(
                "######",
                "#@   #",
                "# 3 :#",
                "######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(2, 2));

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT)); // ends right above the box

        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("6. Caminar en las 4 direcciones alrededor de la WeakBox(3) no descuenta")
    void walkingAroundBoxInFourDirectionsDoesNotCountDown() {
        Level level = TestLevels.level(
                "#######",
                "#@    #",
                "#  3  #",
                "#    :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(2, 3));
        Direction[] loopAroundBox = {
                Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, // above the box
                Direction.DOWN, Direction.DOWN,                    // right of the box
                Direction.LEFT, Direction.LEFT,                    // below the box
                Direction.UP, Direction.UP                         // left of the box
        };

        for (Direction direction : loopAroundBox) {
            assertEquals(MoveResult.OK, level.move(direction), direction.name());
        }

        assertEquals(3, box.getRemainingPushes());
        assertSame(box, level.getBoard().getBoxAt(new Position(2, 3)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("7. Moverse contra una pared o contra % no descuenta")
    void movingIntoWallOrVoidDoesNotCountDown() {
        Level level = TestLevels.level(
                "######",
                "#@%  #",
                "# 3 :#",
                "######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(2, 2));

        assertEquals(MoveResult.BLOCKED, level.move(Direction.UP));
        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("8. Con dos WeakBox(3), empujar una solo descuenta a esa")
    void pushingOneWeakBoxDoesNotAffectTheOther() {
        Level level = TestLevels.level(
                "########",
                "#@3   :#",
                "#  3  :#",
                "########");
        WeakBox pushed = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        WeakBox other = (WeakBox) level.getBoard().getBoxAt(new Position(2, 3));

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertEquals(2, pushed.getRemainingPushes());
        assertEquals(3, other.getRemainingPushes());
    }

    @Test
    @DisplayName("9. Empujar una NormalBox no descuenta a la WeakBox")
    void pushingNormalBoxDoesNotAffectWeakBox() {
        Level level = TestLevels.level(
                "#######",
                "#@$  .#",
                "#  3 :#",
                "#######");
        WeakBox weak = (WeakBox) level.getBoard().getBoxAt(new Position(2, 3));

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertEquals(3, weak.getRemainingPushes());
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("10. Control: empujar la WeakBox(3) con exito si la baja a 2")
    void successfulPushCountsDown() {
        Level level = TestLevels.level(
                "#######",
                "#@3  :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertEquals(2, box.getRemainingPushes());
        assertSame(box, level.getBoard().getBoxAt(new Position(1, 3)));
    }
}
