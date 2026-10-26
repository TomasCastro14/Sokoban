package model.board;

import factory.TestLevels;
import model.game.Level;
import model.tile.StoragePoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Initial visibility: only what the player can reach (plus its outline) is visible when the level loads. */
class HideOnLoadTest {

    /** The player's room on top; a room with a box and a storage point behind a closed door below. */
    private static final String[] ROOM_BEHIND_DOOR = {
            "########",
            "#@     #",
            "####A###",
            "#  $ . #",
            "########"};

    private static boolean visible(Level level, int row, int col) {
        return level.getVisibility().isVisible(new Position(row, col));
    }

    @Test
    @DisplayName("Nivel totalmente abierto: todas las celdas quedan visibles")
    void openLevelIsFullyVisible() {
        Level level = TestLevels.level(
                "######",
                "#@ $ #",
                "#  . #",
                "######");

        for (Cell cell : level.getBoard().cells()) {
            assertTrue(level.getVisibility().isVisible(cell.getPosition()), cell.getPosition().toString());
        }
    }

    @Test
    @DisplayName("La posicion del Sokoban siempre es visible")
    void playerPositionIsVisible() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertTrue(level.getVisibility().isVisible(level.getBoard().getPlayerPosition()));
    }

    @Test
    @DisplayName("La zona alcanzable por el Sokoban es visible")
    void reachableAreaIsVisible() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertTrue(visible(level, 1, 6));
    }

    @Test
    @DisplayName("La habitacion detras de una puerta cerrada arranca oculta")
    void roomBehindClosedDoorIsHidden() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertFalse(visible(level, 3, 1));
    }

    @Test
    @DisplayName("Las paredes que rodean la zona alcanzable son visibles")
    void surroundingWallsAreVisible() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertTrue(visible(level, 0, 0));
        assertTrue(visible(level, 2, 1));
    }

    @Test
    @DisplayName("La puerta cerrada que limita la zona es visible")
    void boundingClosedDoorIsVisible() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertTrue(visible(level, 2, 4));
    }

    @Test
    @DisplayName("Una zona rodeada de paredes, sin acceso, arranca oculta")
    void walledOffAreaIsHidden() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#######",
                "#     #",
                "#######");

        assertFalse(visible(level, 3, 3));
    }

    @Test
    @DisplayName("Cajas y puntos de guardado de la zona alcanzable son visibles")
    void boxAndStorageInReachableAreaAreVisible() {
        Level level = TestLevels.level(
                "#######",
                "#@$ . #",
                "#######");

        assertTrue(visible(level, 1, 2));
        assertTrue(visible(level, 1, 4));
    }

    @Test
    @DisplayName("Cajas y puntos de guardado de la zona oculta quedan ocultos")
    void boxAndStorageInHiddenAreaAreHidden() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertFalse(visible(level, 3, 3));
        assertFalse(visible(level, 3, 5));
    }

    @Test
    @DisplayName("Ocultar no modifica el modelo: la caja oculta sigue en el Board")
    void hiddenBoxStaysOnTheBoard() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertNotNull(level.getBoard().getBoxAt(new Position(3, 3)));
    }

    @Test
    @DisplayName("Ocultar no modifica el modelo: el punto de guardado oculto sigue en el Board")
    void hiddenStoragePointStaysOnTheBoard() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertInstanceOf(StoragePoint.class, level.getBoard().getTile(new Position(3, 5)));
    }

    @Test
    @DisplayName("Cargar el mismo nivel dos veces da la misma visibilidad")
    void loadingTwiceGivesSameVisibility() {
        Level first = TestLevels.level(ROOM_BEHIND_DOOR);
        Level second = TestLevels.level(ROOM_BEHIND_DOOR);

        for (Cell cell : first.getBoard().cells()) {
            Position p = cell.getPosition();
            assertEquals(first.getVisibility().isVisible(p), second.getVisibility().isVisible(p), p.toString());
        }
    }

    @Test
    @DisplayName("Una posicion fuera de la grilla no es visible")
    void positionOutsideTheGridIsNotVisible() {
        Level level = TestLevels.level(ROOM_BEHIND_DOOR);

        assertFalse(visible(level, -1, 0));
        assertFalse(visible(level, 0, 99));
    }
}
