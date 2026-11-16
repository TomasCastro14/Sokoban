package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.NormalBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortalTest {

    @Test
    @DisplayName("1. El jugador que entra en el portal P aparece debajo del otro extremo P")
    void playerTeleports() {
        Level level = TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(2, 6), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Una caja empujada a un portal aparece debajo del otro extremo")
    void boxTeleports() {
        Level level = TestLevels.level(
                "##########",
                "#@$P   P #",
                "#     .  #",
                "##########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(2, 7)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. Los portales funcionan en las dos direcciones")
    void portalsWorkBothWays() {
        Level level = TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");

        level.move(Direction.RIGHT); // enters P (1,2) -> below (1,6) = (2,6)
        level.move(Direction.UP);    // enters P (1,6) -> below (1,2) = (2,2)

        assertEquals(new Position(2, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("4. Si hay varios pares (P, Q...), cada uno solo lleva a su pareja")
    void eachPortalLeadsToItsPair() {
        Level level = TestLevels.level(
                "##########",
                "#@PQ   QP#",
                "#   $.   #",
                "##########");

        level.move(Direction.RIGHT);
        assertEquals(new Position(2, 8), level.getBoard().getPlayerPosition(), "P leads below the other P");

        level.move(Direction.LEFT);  // (2,7), below Q
        level.move(Direction.UP);    // enters Q (1,7) -> below (1,3)
        assertEquals(new Position(2, 3), level.getBoard().getPlayerPosition(), "Q leads below the other Q");
    }

    @Test
    @DisplayName("5. Si la salida esta ocupada y la caja no se puede correr hacia abajo, el movimiento se bloquea")
    void occupiedExitBlocksMovement() {
        Level level = TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");
        level.getBoard().placeBox(new Position(2, 6), new NormalBox()); // below it there is a wall

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }
}
