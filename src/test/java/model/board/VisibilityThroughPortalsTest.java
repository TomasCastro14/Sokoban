package model.board;

import factory.TestLevels;
import model.box.NormalBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/** Rooms closed by walls; the only way between them is a pair of portals. */
class VisibilityThroughPortalsTest {

    private static final String[] TWO_ROOMS_ONE_PAIR = {
            "#######",
            "#@ P  #",
            "#######",
            "#  P  #",
            "#######"};

    private static boolean visible(Level level, int row, int col) {
        return level.getVisibility().isVisible(new Position(row, col));
    }

    private static void openDoorA(Level level) {
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
    }

    @Test
    @DisplayName("Sin portales, una zona separada por paredes sigue oculta como antes")
    void withoutPortalsNothingChanges() {
        Level level = TestLevels.level(
                "#######",
                "#@    #",
                "#######",
                "#     #",
                "#######");

        assertFalse(visible(level, 3, 3));
    }

    @Test
    @DisplayName("La pareja del portal es visible al cargar")
    void pairPortalIsVisible() {
        Level level = TestLevels.level(TWO_ROOMS_ONE_PAIR);

        assertTrue(visible(level, 3, 3));
    }

    @Test
    @DisplayName("El recorrido continua desde la pareja: toda la otra habitacion es visible")
    void reachContinuesFromThePair() {
        Level level = TestLevels.level(TWO_ROOMS_ONE_PAIR);

        assertTrue(visible(level, 3, 1));
        assertTrue(visible(level, 3, 5));
    }

    @Test
    @DisplayName("Los portales funcionan en las dos direcciones")
    void portalsWorkBothWays() {
        Level level = TestLevels.level(
                "#######",
                "#  P  #",
                "#######",
                "#@ P  #",
                "#######");

        assertTrue(visible(level, 1, 5));
    }

    @Test
    @DisplayName("Con varios pares, cada portal lleva solo a su pareja")
    void eachPortalLeadsOnlyToItsPair() {
        Level level = TestLevels.level(
                "#########",
                "#@P     #",
                "#########",
                "#   P   #",
                "#########",
                "#   Q   #",
                "#########",
                "#   Q   #",
                "#########");

        assertTrue(visible(level, 3, 1));
        assertFalse(visible(level, 5, 1));
        assertFalse(visible(level, 7, 1));
    }

    @Test
    @DisplayName("Un portal detras de una puerta cerrada no revela su pareja")
    void portalBehindClosedDoorKeepsPairHidden() {
        Level level = TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  P   #",
                "########",
                "#  P   #",
                "########");

        assertFalse(visible(level, 5, 1));
    }

    @Test
    @DisplayName("Al abrir la puerta que daba al portal se revela la zona de la pareja")
    void openingDoorToPortalRevealsPairRoom() {
        Level level = TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  P   #",
                "########",
                "#  P   #",
                "########");

        openDoorA(level);

        assertTrue(visible(level, 5, 1));
    }

    @Test
    @DisplayName("Una puerta cerrada del otro lado del portal bloquea igual")
    void closedDoorOnTheOtherSideBlocks() {
        Level level = TestLevels.level(
                "########",
                "#@ P   #",
                "########",
                "# P A  #",
                "########");

        assertTrue(visible(level, 3, 1));
        assertTrue(visible(level, 3, 4));
        assertFalse(visible(level, 3, 6));
    }

    @Test
    @DisplayName("Portales en ciclo: el calculo termina")
    void portalsInACycleTerminate() {
        Level level = TestLevels.level(
                "#######",
                "#@P Q #",
                "#######",
                "#Q P  #",
                "#######");

        assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> new Reachability().from(level.getBoard(), level.getBoard().getPlayerPosition()));
        assertTrue(visible(level, 3, 5));
    }

    @Test
    @DisplayName("Una caja en la salida del portal no corta la visibilidad (ver no es moverse)")
    void boxOnPairPortalDoesNotBlockVision() {
        Level level = TestLevels.level(TWO_ROOMS_ONE_PAIR);
        Board board = level.getBoard();
        board.placeBox(new Position(3, 3), new NormalBox());

        assertTrue(new Reachability().from(board, board.getPlayerPosition()).contains(new Position(3, 1)));
    }

    @Test
    @DisplayName("Un portal sin pareja no conecta con nada")
    void unpairedPortalLeadsNowhere() {
        Level level = TestLevels.level(
                "#######",
                "#@ P  #",
                "#######",
                "#     #",
                "#######");

        assertFalse(visible(level, 3, 3));
    }
}
