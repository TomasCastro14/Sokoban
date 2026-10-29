package model.tile;

import factory.TestLevels;
import model.board.Board;
import model.board.Cell;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.NormalBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A pair has no fixed entrance: entering either portal leads to the other one.
 * Boards that need several moves keep a box away from its storage point, so the level is not won at once.
 */
class PortalSymmetryTest {

    private static final Position A = new Position(1, 2);
    private static final Position B = new Position(1, 6);

    /** Player left of A (1,2), pair B at (1,6). */
    private static final String[] PLAYER_NEXT_TO_A = {
            "#########",
            "#@P   P #",
            "#  $.   #",
            "#########"};

    /** Player right of B (1,6), pair A at (1,2). */
    private static final String[] PLAYER_NEXT_TO_B = {
            "#########",
            "# P   P@#",
            "#  $.   #",
            "#########"};

    private static Position move(Level level, Direction... moves) {
        for (Direction d : moves) {
            level.move(d);
        }
        return level.getBoard().getPlayerPosition();
    }

    @Test
    @DisplayName("Entrar en A lleva a B")
    void enteringALeadsToB() {
        assertEquals(B, move(TestLevels.level(PLAYER_NEXT_TO_A), Direction.RIGHT));
    }

    @Test
    @DisplayName("Entrar en B lleva a A")
    void enteringBLeadsToA() {
        assertEquals(A, move(TestLevels.level(PLAYER_NEXT_TO_B), Direction.LEFT));
    }

    @Test
    @DisplayName("Ida y vuelta: A lleva a B, sale de B y al volver a entrar en B llega a A")
    void roundTrip() {
        Level level = TestLevels.level(PLAYER_NEXT_TO_A);

        assertEquals(A, move(level, Direction.RIGHT, Direction.RIGHT, Direction.LEFT));
    }

    @Test
    @DisplayName("Al aparecer en la pareja no hay un segundo salto (sin ping-pong)")
    void arrivingDoesNotTeleportAgain() {
        Level level = TestLevels.level(PLAYER_NEXT_TO_A);

        assertEquals(B, move(level, Direction.RIGHT));
        assertEquals(1, level.getStats().getMoves());
    }

    @Test
    @DisplayName("Una caja empujada a A aparece en B")
    void boxIntoALandsOnB() {
        Level level = TestLevels.level(
                "##########",
                "#@$P   P #",
                "##########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 7)));
    }

    @Test
    @DisplayName("Una caja empujada a B aparece en A")
    void boxIntoBLandsOnA() {
        Level level = TestLevels.level(
                "##########",
                "# P   P$@#",
                "##########");

        level.move(Direction.LEFT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
    }

    @Test
    @DisplayName("Entrar por A o por B suma lo mismo: 1 movimiento, 0 empujes")
    void sameCountersBothWays() {
        Level viaA = TestLevels.level(PLAYER_NEXT_TO_A);
        Level viaB = TestLevels.level(PLAYER_NEXT_TO_B);

        viaA.move(Direction.RIGHT);
        viaB.move(Direction.LEFT);

        assertEquals(viaA.getStats().getMoves(), viaB.getStats().getMoves());
        assertEquals(viaA.getStats().getPushes(), viaB.getStats().getPushes());
    }

    @Test
    @DisplayName("Salida ocupada entrando por A: BLOCKED")
    void occupiedExitViaA() {
        Level level = TestLevels.level(PLAYER_NEXT_TO_A);
        level.getBoard().placeBox(B, new NormalBox());

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
    }

    @Test
    @DisplayName("Salida ocupada entrando por B: BLOCKED")
    void occupiedExitViaB() {
        Level level = TestLevels.level(PLAYER_NEXT_TO_B);
        level.getBoard().placeBox(A, new NormalBox());

        assertEquals(MoveResult.BLOCKED, level.move(Direction.LEFT));
    }

    @Test
    @DisplayName("Con varios pares, la simetria se cumple en cada par: entrar por la pareja de Q vuelve al primer Q")
    void symmetryPerPair() {
        Level level = TestLevels.level(
                "###########",
                "#QP     PQ#",
                "#@        #",
                "#   $.    #",
                "###########");

        level.move(Direction.UP); // enters Q (1,1) -> Q (1,9)
        level.move(Direction.DOWN);
        level.move(Direction.UP); // enters Q (1,9) again -> Q (1,1)

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Invariante al cargar: la pareja de la pareja de cada portal es el mismo portal")
    void pairOfPairIsItself() {
        Board board = TestLevels.level(
                "############",
                "#@PQ R  RQP#",
                "############").getBoard();

        for (Cell cell : board.cells()) {
            for (Position pair : cell.getTile().linkedPositions()) {
                assertEquals(Set.of(cell.getPosition()), board.getTile(pair).linkedPositions(), cell.getPosition().toString());
            }
        }
    }

    @Test
    @DisplayName("Portal.link vincula los dos sentidos en un solo paso")
    void linkConnectsBothWays() {
        Portal a = new Portal('P');
        Portal b = new Portal('P');

        Portal.link(a, A, b, B);

        assertEquals(Set.of(B), a.linkedPositions());
        assertEquals(Set.of(A), b.linkedPositions());
    }

    @Test
    @DisplayName("Un portal ya vinculado no se puede volver a vincular")
    void alreadyLinkedPortalCannotBeRelinked() {
        Portal a = new Portal('P');
        Portal b = new Portal('P');
        Portal.link(a, A, b, B);

        assertThrows(IllegalStateException.class, () -> Portal.link(a, A, new Portal('P'), new Position(2, 2)));
    }
}
