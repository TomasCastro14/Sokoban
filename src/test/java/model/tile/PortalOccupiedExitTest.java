package model.tile;

import factory.TestLevels;
import model.board.Board;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.NormalBox;
import model.box.WeakBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Rule: the exit is the pair portal's cell. If something is already there,
 * the move is BLOCKED and nothing changes, like pushing two boxes in a row.
 */
class PortalOccupiedExitTest {

    /** P at (1,2) and its pair at (1,6). */
    private static final String[] PLAYER_NEXT_TO_PORTAL = {
            "#########",
            "#@P   P #",
            "#########"};

    /** Box at (1,2) in front of P (1,3); pair at (1,7). */
    private static final String[] BOX_NEXT_TO_PORTAL = {
            "##########",
            "#@$P   P #",
            "##########"};

    private static final Position PLAYER_START = new Position(1, 1);

    private static Level levelWithBoxAt(Position p, String... rows) {
        Level level = TestLevels.level(rows);
        level.getBoard().placeBox(p, new NormalBox());
        return level;
    }

    @Test
    @DisplayName("Jugador entra al portal con una caja en la salida: BLOCKED")
    void playerIntoOccupiedExitIsBlocked() {
        Level level = levelWithBoxAt(new Position(1, 6), PLAYER_NEXT_TO_PORTAL);

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
    }

    @Test
    @DisplayName("Jugador bloqueado por la salida ocupada: no se mueve")
    void blockedPlayerStaysPut() {
        Level level = levelWithBoxAt(new Position(1, 6), PLAYER_NEXT_TO_PORTAL);

        level.move(Direction.RIGHT);

        assertEquals(PLAYER_START, level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Jugador bloqueado por la salida ocupada: la caja de la salida no se mueve")
    void boxAtExitStaysPut() {
        Level level = levelWithBoxAt(new Position(1, 6), PLAYER_NEXT_TO_PORTAL);

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 6)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 7)));
    }

    @Test
    @DisplayName("Jugador bloqueado por la salida ocupada: no suma movimientos ni empujes")
    void blockedPlayerCountsNothing() {
        Level level = levelWithBoxAt(new Position(1, 6), PLAYER_NEXT_TO_PORTAL);

        level.move(Direction.RIGHT);

        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("Una WeakBox en la salida no descuenta cuando el jugador queda bloqueado")
    void weakBoxAtExitKeepsItsPushes() {
        Level level = TestLevels.level(PLAYER_NEXT_TO_PORTAL);
        WeakBox weak = new WeakBox(3);
        level.getBoard().placeBox(new Position(1, 6), weak);

        level.move(Direction.RIGHT);

        assertEquals(3, weak.getRemainingPushes());
    }

    @Test
    @DisplayName("Caja empujada al portal con otra caja en la salida: BLOCKED")
    void boxIntoOccupiedExitIsBlocked() {
        Level level = levelWithBoxAt(new Position(1, 7), BOX_NEXT_TO_PORTAL);

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
    }

    @Test
    @DisplayName("Caja bloqueada por la salida ocupada: las dos cajas y el jugador quedan donde estaban")
    void blockedBoxPushChangesNoPosition() {
        Level level = levelWithBoxAt(new Position(1, 7), BOX_NEXT_TO_PORTAL);
        Board board = level.getBoard();

        level.move(Direction.RIGHT);

        assertNotNull(board.getBoxAt(new Position(1, 2)));
        assertNotNull(board.getBoxAt(new Position(1, 7)));
        assertEquals(PLAYER_START, board.getPlayerPosition());
    }

    @Test
    @DisplayName("Caja bloqueada por la salida ocupada: no suma movimientos ni empujes")
    void blockedBoxPushCountsNothing() {
        Level level = levelWithBoxAt(new Position(1, 7), BOX_NEXT_TO_PORTAL);

        level.move(Direction.RIGHT);

        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("Una WeakBox empujada a un portal con la salida ocupada no descuenta")
    void blockedWeakBoxKeepsItsPushes() {
        Level level = TestLevels.level(
                "##########",
                "#@3P   P #",
                "##########");
        level.getBoard().placeBox(new Position(1, 7), new NormalBox());
        WeakBox weak = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        level.move(Direction.RIGHT);

        assertEquals(3, weak.getRemainingPushes());
    }

    @Test
    @DisplayName("En la otra direccion: entrando por la pareja con una caja en P, tambien BLOCKED")
    void occupiedExitBlocksTheOtherWayToo() {
        Level level = levelWithBoxAt(new Position(1, 2),
                "#########",
                "# P   P@#",
                "#########");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.LEFT));
        assertEquals(new Position(1, 7), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Con varios pares, la salida ocupada de P no afecta a Q")
    void occupiedExitOfPDoesNotAffectQ() {
        Level level = levelWithBoxAt(new Position(1, 8),
                "##########",
                "#@P   Q P#",
                "#Q       #",
                "##########");

        assertEquals(MoveResult.OK, level.move(Direction.DOWN));
        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Salida libre: el jugador sigue apareciendo en la pareja")
    void freeExitStillTeleportsPlayer() {
        Level level = TestLevels.level(PLAYER_NEXT_TO_PORTAL);

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Salida libre: la caja sigue apareciendo en la pareja y cuenta un empuje")
    void freeExitStillTeleportsBox() {
        Level level = TestLevels.level(BOX_NEXT_TO_PORTAL);

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 7)));
        assertEquals(1, level.getStats().getPushes());
    }
}
