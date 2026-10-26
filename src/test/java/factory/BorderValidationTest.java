package factory;

import exception.InvalidLevelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Map rows start at file line HEADER_LINES + 1, so row r is reported as HEADER_LINES + 1 + r. */
class BorderValidationTest {
    private static final int FIRST_ROW_LINE = TestLevels.HEADER_LINES + 1;

    private final LevelLoader loader = new LevelLoader();

    private void assertValid(String... rows) {
        assertDoesNotThrow(() -> loader.parse(TestLevels.text(rows)));
    }

    /** Asserts the level breaks the border rule exactly at the given map row. */
    private void assertOpenBorderAt(int mapRow, String... rows) {
        InvalidLevelException e = assertThrows(InvalidLevelException.class, () -> loader.parse(TestLevels.text(rows)));
        assertTrue(e.getMessage().startsWith(BorderRule.RULE), e.getMessage());
        assertEquals(FIRST_ROW_LINE + mapRow, e.getLine());
    }

    @Test
    @DisplayName("1. Un nivel con todo el borde de # carga sin error")
    void wallBorderIsValid() {
        assertValid(
                "#####",
                "#@$.#",
                "#####");
    }

    @Test
    @DisplayName("2. Un nivel con borde mezclado de # y % carga sin error")
    void wallAndVoidBorderIsValid() {
        assertValid(
                "%###%",
                "#@$.#",
                "%###%");
    }

    @Test
    @DisplayName("3. Piso en la primera fila da error en la linea de esa fila")
    void floorInFirstRow() {
        assertOpenBorderAt(0,
                "## ##",
                "#@$.#",
                "#####");
    }

    @Test
    @DisplayName("4. Piso en la ultima fila da error en la linea de esa fila")
    void floorInLastRow() {
        assertOpenBorderAt(2,
                "#####",
                "#@$.#",
                "## ##");
    }

    @Test
    @DisplayName("5. Piso en la primera columna de una fila del medio da error en esa linea")
    void floorInFirstColumn() {
        assertOpenBorderAt(1,
                "#####",
                " @$.#",
                "#####");
    }

    @Test
    @DisplayName("6. Piso en la ultima columna de una fila del medio da error en esa linea")
    void floorInLastColumn() {
        assertOpenBorderAt(1,
                "#####",
                "#@$. ",
                "#####");
    }

    @Test
    @DisplayName("7. Una caja en el borde da error")
    void boxOnBorder() {
        assertOpenBorderAt(1,
                "######",
                "#@ . $",
                "######");
    }

    @Test
    @DisplayName("8. El jugador en el borde da error")
    void playerOnBorder() {
        assertOpenBorderAt(1,
                "#####",
                "@$. #",
                "#####");
    }

    @Test
    @DisplayName("9. Un punto de guardado en el borde da error")
    void storagePointOnBorder() {
        assertOpenBorderAt(0,
                "##.##",
                "#@$ #",
                "#####");
    }

    @Test
    @DisplayName("10. Una esquina que no es # ni % da error")
    void openCorner() {
        assertOpenBorderAt(2,
                "#####",
                "#@$.#",
                "#### ");
    }

    @Test
    @DisplayName("11. Mapa irregular valido: lo que le falta a una fila corta cuenta como %")
    void irregularMapWithShortRowIsValid() {
        assertValid(
                "#####",
                "#@$.#",
                "####");
    }

    @Test
    @DisplayName("12. Mapa irregular invalido: una fila mas larga que termina en piso deja el borde abierto")
    void irregularMapWithLongerOpenRow() {
        assertOpenBorderAt(1,
                "####",
                "#@$. ",
                "####");
    }

    @Test
    @DisplayName("13. Con varias celdas abiertas en el borde se reporta la primera en orden de lectura")
    void reportsFirstOpenCellInReadingOrder() {
        assertOpenBorderAt(0,
                "## ##",
                " @$.#",
                "#####");
    }

    @Test
    @DisplayName("14. Un caracter desconocido se reporta antes que el borde abierto")
    void unknownCharacterWinsOverBorder() {
        InvalidLevelException e = assertThrows(InvalidLevelException.class, () -> loader.parse(TestLevels.text(
                "## ##",
                "#@$?#",
                "#####")));

        assertTrue(e.getMessage().startsWith("Unknown character"), e.getMessage());
    }

    @Test
    @DisplayName("15. El borde abierto se reporta antes que la cantidad de jugadores")
    void borderWinsOverPlayerCount() {
        assertOpenBorderAt(0,
                "## ##",
                "# $.#",
                "#####");
    }
}
