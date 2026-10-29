package factory;

import model.board.Board;
import model.board.Cell;
import model.board.Position;
import model.tile.Portal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Linking pairs when the level loads. Invalid pairs (1 or 3+ of a letter) are covered by PortalPairRuleTest.
 * Regression tests: the linking already exists in LevelBuilder.linkPortals / Portal.link.
 */
class PortalLinkingTest {

    private static Board load(String... rows) {
        return TestLevels.level(rows).getBoard();
    }

    private static Position pairOf(Board board, Position portal) {
        Set<Position> linked = board.getTile(portal).linkedPositions();
        assertEquals(1, linked.size(), "portal at " + portal + " must have exactly one pair");
        return linked.iterator().next();
    }

    /** Every portal on the board, with its pair. */
    private static Map<Position, Position> pairs(Board board) {
        Map<Position, Position> pairs = new HashMap<>();
        for (Cell cell : board.cells()) {
            if (cell.getTile() instanceof Portal) {
                pairs.put(cell.getPosition(), pairOf(board, cell.getPosition()));
            }
        }
        return pairs;
    }

    @Test
    @DisplayName("Un par P-P: cada P queda vinculado con el otro, en fila y columna exactas")
    void onePairIsLinked() {
        Board board = load(
                "########",
                "#@P  P #",
                "########");

        assertEquals(new Position(1, 5), pairOf(board, new Position(1, 2)));
        assertEquals(new Position(1, 2), pairOf(board, new Position(1, 5)));
    }

    @Test
    @DisplayName("Simetria: la pareja de la pareja de cada portal es el mismo portal")
    void pairOfPairIsItself() {
        Board board = load(
                "##########",
                "#@PQ R RQP#",
                "##########");

        pairs(board).forEach((portal, pair) -> assertEquals(portal, pairOf(board, pair), portal.toString()));
    }

    @Test
    @DisplayName("Ningun portal es pareja de si mismo")
    void noPortalIsItsOwnPair() {
        Board board = load(
                "##########",
                "#@PQ R RQP#",
                "##########");

        pairs(board).forEach((portal, pair) -> assertNotEquals(portal, pair));
    }

    /** Layout L abc cba L: the pair of the first L must be the last L, never a neighbor of another letter. */
    @ParameterizedTest(name = "Con los 4 pares, {0} queda vinculado con el otro {0}")
    @ValueSource(chars = {'P', 'Q', 'R', 'S'})
    void eachLetterLinksOnlyWithItsOwnLetter(char letter) {
        String others = "PQRS".replace(String.valueOf(letter), "");
        String row = "#@" + letter + others + new StringBuilder(others).reverse() + letter + "#";
        Board board = load("#".repeat(row.length()), row, "#".repeat(row.length()));

        assertEquals(new Position(1, row.length() - 2), pairOf(board, new Position(1, 2)));
    }

    @Test
    @DisplayName("Orden P, Q, P, Q en la misma fila")
    void interleavedOrder() {
        Board board = load(
                "##########",
                "#@P Q P Q#",
                "##########");

        assertEquals(new Position(1, 6), pairOf(board, new Position(1, 2)));
        assertEquals(new Position(1, 8), pairOf(board, new Position(1, 4)));
    }

    @Test
    @DisplayName("Orden P, P, Q, Q en la misma fila")
    void groupedOrder() {
        Board board = load(
                "##########",
                "#@P P Q Q#",
                "##########");

        assertEquals(new Position(1, 4), pairOf(board, new Position(1, 2)));
        assertEquals(new Position(1, 8), pairOf(board, new Position(1, 6)));
    }

    @Test
    @DisplayName("Pares repartidos en distintas filas")
    void pairsAcrossRows() {
        Board board = load(
                "######",
                "#@P Q#",
                "#    #",
                "#Q P #",
                "######");

        assertEquals(new Position(3, 3), pairOf(board, new Position(1, 2)));
        assertEquals(new Position(3, 1), pairOf(board, new Position(1, 4)));
    }

    @Test
    @DisplayName("Un nivel sin portales carga bien")
    void levelWithoutPortalsLoads() {
        Board board = load(
                "#####",
                "#@$.#",
                "#####");

        assertTrue(pairs(board).isEmpty());
    }
}
