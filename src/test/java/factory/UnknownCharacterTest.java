package factory;

import exception.InvalidLevelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Map rows start at file line HEADER_LINES + 1, so row r is reported as HEADER_LINES + 1 + r. */
class UnknownCharacterTest {
    private static final int FIRST_ROW_LINE = TestLevels.HEADER_LINES + 1;

    private final LevelLoader loader = new LevelLoader();

    /** Asserts the level is rejected for an unknown character at the given map row. */
    private void assertUnknownAt(int mapRow, char symbol, String... rows) {
        InvalidLevelException e = assertThrows(InvalidLevelException.class,
                () -> loader.parse(TestLevels.text(rows)), "'" + symbol + "'");
        assertTrue(e.getMessage().startsWith(LevelLoader.UNKNOWN_CHARACTER), e.getMessage());
        assertTrue(e.getMessage().contains("'" + symbol + "'"), e.getMessage());
        assertEquals(FIRST_ROW_LINE + mapRow, e.getLine(), "'" + symbol + "'");
    }

    /** A closed one-row level with the given symbol next to the player. */
    private void assertSymbolIsUnknown(char symbol) {
        assertUnknownAt(1, symbol,
                "######",
                "#@$." + symbol + "#",
                "######");
    }

    @Test
    @DisplayName("1. Un nivel con todos los caracteres del formato (incluido el espacio) carga sin error")
    void everyFormatCharacterIsAccepted() {
        assertDoesNotThrow(() -> loader.parse(TestLevels.text(
                "##################",
                "#@$K123456789.:;~#",
                "#abcdeABCDE PPQQ #",
                "#RRSS            #",
                "%%%%%%%%%%%%%%%%%%")));
    }

    @Test
    @DisplayName("2. Caracteres claramente invalidos (X, !, *) se rechazan con la regla y la linea")
    void obviousInvalidCharacters() {
        assertSymbolIsUnknown('X');
        assertSymbolIsUnknown('!');
        assertSymbolIsUnknown('*');
    }

    @Test
    @DisplayName("3. Justo fuera de los rangos (f, F, T) se rechaza")
    void justOutsideLetterRanges() {
        assertSymbolIsUnknown('f');
        assertSymbolIsUnknown('F');
        assertSymbolIsUnknown('T');
    }

    @Test
    @DisplayName("4. El 0 se rechaza: la WeakBox va de 1 a 9")
    void zeroIsNotAWeakBox() {
        assertSymbolIsUnknown('0');
    }

    @Test
    @DisplayName("5. La k minuscula se rechaza: la KeyBox es K mayuscula")
    void lowercaseKeyBoxIsUnknown() {
        assertSymbolIsUnknown('k');
    }

    @Test
    @DisplayName("6. Un tab dentro del mapa se rechaza")
    void tabIsUnknown() {
        assertSymbolIsUnknown('\t');
    }

    @Test
    @DisplayName("7. Un caracter desconocido en la primera fila del mapa reporta esa linea")
    void unknownInFirstRow() {
        assertUnknownAt(0, 'X',
                "##X###",
                "#@$. #",
                "######");
    }

    @Test
    @DisplayName("8. Un caracter desconocido en una fila del medio reporta esa linea")
    void unknownInMiddleRow() {
        assertUnknownAt(2, 'X',
                "######",
                "#@$. #",
                "#  X #",
                "#    #",
                "######");
    }

    @Test
    @DisplayName("9. Un caracter desconocido en la ultima fila reporta esa linea")
    void unknownInLastRow() {
        assertUnknownAt(2, 'X',
                "######",
                "#@$. #",
                "####X#");
    }

    @Test
    @DisplayName("10. Con varios caracteres desconocidos se reporta el primero en orden de lectura")
    void reportsFirstUnknownInReadingOrder() {
        assertUnknownAt(1, 'Y',
                "######",
                "#@Y.Z#",
                "#X   #",
                "######");
    }

    @Test
    @DisplayName("11. Un caracter raro en el encabezado no dispara esta validacion")
    void headerIsNotChecked() {
        String text = "nombre: Nivel ¡raro! X*\n"
                + "tiempo: 90\n"
                + "objetivo: 500\n"
                + "---\n"
                + "######\n"
                + "#@$. #\n"
                + "######";

        assertDoesNotThrow(() -> loader.parse(text));
    }

    @Test
    @DisplayName("12. Un archivo con finales de linea de Windows (\\r\\n) carga sin error")
    void windowsLineEndingsAreAccepted() {
        String text = TestLevels.text(
                "######",
                "#@$. #",
                "######").replace("\n", "\r\n") + "\r\n";

        assertDoesNotThrow(() -> loader.parse(text));
    }
}
