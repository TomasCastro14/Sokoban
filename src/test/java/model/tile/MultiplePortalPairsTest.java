package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/** Several pairs in the same level: each portal leads only to the other portal with its letter. */
class MultiplePortalPairsTest {

    private static Position playerAfter(Level level, Direction... moves) {
        for (Direction d : moves) {
            level.move(d);
        }
        return level.getBoard().getPlayerPosition();
    }

    @Test
    @DisplayName("Con P y Q, entrar en P lleva al otro P y no a un Q")
    void pLeadsToPNotQ() {
        Level level = TestLevels.level(
                "##########",
                "#@PQ   QP#",
                "##########");

        assertEquals(new Position(1, 8), playerAfter(level, Direction.RIGHT));
    }

    @Test
    @DisplayName("Con P y Q, entrar en Q lleva al otro Q")
    void qLeadsToQ() {
        Level level = TestLevels.level(
                "##########",
                "#@QP   PQ#",
                "##########");

        assertEquals(new Position(1, 8), playerAfter(level, Direction.RIGHT));
    }

    @Test
    @DisplayName("Entrar por el otro extremo de P lleva al primer P")
    void pWorksTheOtherWay() {
        Level level = TestLevels.level(
                "##########",
                "#P Q  QP@#",
                "##########");

        assertEquals(new Position(1, 1), playerAfter(level, Direction.LEFT));
    }

    @Test
    @DisplayName("Entrar por el otro extremo de Q lleva al primer Q")
    void qWorksTheOtherWay() {
        Level level = TestLevels.level(
                "##########",
                "#Q P  PQ@#",
                "##########");

        assertEquals(new Position(1, 1), playerAfter(level, Direction.LEFT));
    }

    @Test
    @DisplayName("Una caja empujada a P aparece en la pareja de P")
    void boxIntoPAppearsAtP() {
        Level level = TestLevels.level(
                "###########",
                "#@$P Q  QP#",
                "###########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 9)));
    }

    @Test
    @DisplayName("Una caja empujada a Q aparece en la pareja de Q")
    void boxIntoQAppearsAtQ() {
        Level level = TestLevels.level(
                "###########",
                "#@$Q P  PQ#",
                "###########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 9)));
    }

    /**
     * The entered portal sits right next to the three other pairs: L abc cba L.
     * Entering L must land on the other L, never on a neighbor of another pair.
     */
    @ParameterizedTest(name = "Con los 4 pares, {0} lleva a su pareja")
    @ValueSource(chars = {'P', 'Q', 'R', 'S'})
    void eachOfTheFourPairsLeadsToItsPair(char letter) {
        String others = "PQRS".replace(String.valueOf(letter), "");
        String row = "#@" + letter + others + new StringBuilder(others).reverse() + letter + "#";
        Level level = TestLevels.level("#".repeat(row.length()), row, "#".repeat(row.length()));

        assertEquals(new Position(1, row.length() - 2), playerAfter(level, Direction.RIGHT));
    }

    @Test
    @DisplayName("El orden en el mapa P, Q, P, Q no cambia el destino")
    void interleavedOrder() {
        Level level = TestLevels.level(
                "###########",
                "#@P Q P Q #",
                "###########");

        assertEquals(new Position(1, 6), playerAfter(level, Direction.RIGHT));
    }

    @Test
    @DisplayName("El orden en el mapa P, P, Q, Q no cambia el destino")
    void groupedOrder() {
        Level level = TestLevels.level(
                "###########",
                "#@P P Q Q #",
                "###########");

        assertEquals(new Position(1, 4), playerAfter(level, Direction.RIGHT));
    }

}
