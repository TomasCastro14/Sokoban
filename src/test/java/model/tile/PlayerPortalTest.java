package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The player entering a portal appears on the pair portal, in the same move. */
class PlayerPortalTest {

    private static Level level() {
        return TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");
    }

    @Test
    @DisplayName("Entrar en P deja al jugador sobre el otro P")
    void playerAppearsOnThePairPortal() {
        Level level = level();

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition());
        assertInstanceOf(Portal.class, level.getBoard().getTile(new Position(1, 6)));
    }

    @Test
    @DisplayName("El teletransporte es una sola accion: suma 1 movimiento y ningun empuje")
    void teleportCountsOneMoveAndNoPush() {
        Level level = level();

        level.move(Direction.RIGHT);

        assertEquals(1, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("Aparecer sobre el portal de salida no lo activa: no hay rebote")
    void arrivingOnThePortalDoesNotTeleportAgain() {
        Level level = level();

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition(), "stays on the exit, no bounce back to the entry");
    }

    @Test
    @DisplayName("El jugador se aleja y vuelve a entrar: se teletransporta de nuevo")
    void leavingAndEnteringAgainTeleportsAgain() {
        Level level = level();
        level.move(Direction.RIGHT); // (1,1) -> enters P (1,2) -> appears on (1,6)
        level.move(Direction.LEFT);  // steps away to (1,5)

        level.move(Direction.RIGHT); // enters (1,6) again -> appears on its pair (1,2)

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertEquals(3, level.getStats().getMoves());
    }
}
