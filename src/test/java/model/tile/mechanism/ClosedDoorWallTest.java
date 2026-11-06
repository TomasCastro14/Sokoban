package model.tile.mechanism;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.WeakBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** A closed door is a wall, an open door is floor. The door answers it through allowsPlayer / allowsBox. */
class ClosedDoorWallTest {

    private static void open(Level level, int row, int col) {
        ((Door) level.getBoard().getTile(new Position(row, col))).open();
    }

    @Test
    @DisplayName("El jugador contra una puerta cerrada: BLOCKED y no suma movimiento")
    void playerBlockedByClosedDoor() {
        Level level = TestLevels.level(
                "#######",
                "#@A a.#",
                "#  $  #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getMoves());
    }

    @Test
    @DisplayName("Una NormalBox contra una puerta cerrada: BLOCKED y no suma empuje")
    void normalBoxBlockedByClosedDoor() {
        Level level = TestLevels.level(
                "#######",
                "#@$A a#",
                "#  .  #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(0, level.getStats().getPushes());
        assertEquals(0, level.getStats().getMoves());
    }

    @Test
    @DisplayName("Una WeakBox contra una puerta cerrada no descuenta empujes")
    void weakBoxKeepsItsPushesAgainstClosedDoor() {
        Level level = TestLevels.level(
                "#######",
                "#@3A a#",
                "#  .  #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        assertEquals(3, box.getRemainingPushes());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("El jugador que se desliza por hielo frena antes de una puerta cerrada")
    void iceSlideStopsBeforeClosedDoor() {
        Level level = TestLevels.level(
                "#########",
                "#@~~A a #",
                "#   .   #",
                "#########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Una puerta abierta es piso: pasan el jugador y una caja")
    void openDoorLetsPlayerAndBoxesThrough() {
        Level level = TestLevels.level(
                "########",
                "#@$A a.#",
                "#  ;   #",
                "########");
        open(level, 1, 3);

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
    }
}
