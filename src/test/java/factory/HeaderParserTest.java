package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import model.game.LevelHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** The header lines are the first lines of the file, so line N of the list is line N of the file. */
class HeaderParserTest {

    private final HeaderParser parser = new HeaderParser();

    private LevelHeader parse(String... lines) throws InvalidLevelException {
        return parser.parse(List.of(lines));
    }

    private void assertRejected(LevelFormatRule rule, int line, String... lines) {
        InvalidLevelException e = assertThrows(InvalidLevelException.class, () -> parse(lines));
        assertEquals(rule, e.getRule(), e.getMessage());
        assertEquals(line, e.getLine(), e.getMessage());
    }

    @Test
    @DisplayName("1. Un encabezado valido da nombre, tiempo (Duration) y objetivo ya tipados")
    void validHeaderIsTyped() throws InvalidLevelException {
        LevelHeader header = parse("nombre: Nivel 1", "tiempo: 90", "objetivo: 500");

        assertEquals("Nivel 1", header.name());
        assertEquals(Duration.ofSeconds(90), header.timeLimit());
        assertEquals(500, header.targetScore());
    }

    @Test
    @DisplayName("2. El nombre conserva espacios, tildes y ñ")
    void nameKeepsAccentsAndSpaces() throws InvalidLevelException {
        assertEquals("El año del ñandú", parse("nombre: El año del ñandú", "tiempo: 90", "objetivo: 500").name());
    }

    @Test
    @DisplayName("3. Se ignoran los espacios alrededor de clave y valor, y las lineas vacias")
    void extraSpacesAndBlankLinesAreIgnored() throws InvalidLevelException {
        LevelHeader header = parse("  nombre  :   Nivel 1  ", "", "tiempo:90", "   ", " objetivo : 500 ");

        assertEquals("Nivel 1", header.name());
        assertEquals(Duration.ofSeconds(90), header.timeLimit());
        assertEquals(500, header.targetScore());
    }

    @Test
    @DisplayName("4. Los campos pueden venir en cualquier orden")
    void fieldsInAnyOrder() throws InvalidLevelException {
        LevelHeader header = parse("objetivo: 500", "nombre: Nivel 1", "tiempo: 90");

        assertEquals("Nivel 1", header.name());
    }

    @Test
    @DisplayName("5. Si falta el nombre se rechaza (regla global, sin linea)")
    void missingName() {
        assertRejected(LevelFormatRule.HEADER_MISSING_FIELD, InvalidLevelException.NO_LINE, "tiempo: 90", "objetivo: 500");
    }

    @Test
    @DisplayName("6. Si falta el tiempo se rechaza")
    void missingTime() {
        assertRejected(LevelFormatRule.HEADER_MISSING_FIELD, InvalidLevelException.NO_LINE, "nombre: N", "objetivo: 500");
    }

    @Test
    @DisplayName("7. Si falta el objetivo se rechaza")
    void missingTarget() {
        assertRejected(LevelFormatRule.HEADER_MISSING_FIELD, InvalidLevelException.NO_LINE, "nombre: N", "tiempo: 90");
    }

    @Test
    @DisplayName("8. Un campo repetido se rechaza en la linea de la repeticion")
    void duplicateField() {
        assertRejected(LevelFormatRule.HEADER_DUPLICATE_FIELD, 3, "nombre: N", "tiempo: 90", "tiempo: 60", "objetivo: 500");
    }

    @Test
    @DisplayName("9. Una clave desconocida se rechaza en su linea")
    void unknownField() {
        assertRejected(LevelFormatRule.HEADER_UNKNOWN_FIELD, 2, "nombre: N", "autor: Ana", "tiempo: 90", "objetivo: 500");
    }

    @Test
    @DisplayName("10. Las claves distinguen mayusculas: 'Nombre' es desconocida")
    void keysAreCaseSensitive() {
        assertRejected(LevelFormatRule.HEADER_UNKNOWN_FIELD, 1, "Nombre: N", "tiempo: 90", "objetivo: 500");
    }

    @Test
    @DisplayName("11. Un tiempo no numerico se rechaza en la linea del tiempo")
    void timeNotNumeric() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 2, "nombre: N", "tiempo: abc", "objetivo: 500");
    }

    @Test
    @DisplayName("12. Un tiempo negativo o cero se rechaza en la linea del tiempo")
    void timeNotPositive() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 2, "nombre: N", "tiempo: -5", "objetivo: 500");
        assertRejected(LevelFormatRule.INVALID_HEADER, 2, "nombre: N", "tiempo: 0", "objetivo: 500");
    }

    @Test
    @DisplayName("13. Un tiempo con formato mm:ss no es valido (son segundos enteros)")
    void timeWithMinutesFormat() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 2, "nombre: N", "tiempo: 1:30", "objetivo: 500");
    }

    @Test
    @DisplayName("14. Un objetivo invalido (texto o negativo) se rechaza en la linea del objetivo")
    void invalidTarget() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 3, "nombre: N", "tiempo: 90", "objetivo: mucho");
        assertRejected(LevelFormatRule.INVALID_HEADER, 3, "nombre: N", "tiempo: 90", "objetivo: -1");
    }

    @Test
    @DisplayName("15. Un nombre vacio se rechaza en la linea del nombre")
    void emptyName() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 1, "nombre:   ", "tiempo: 90", "objetivo: 500");
    }

    @Test
    @DisplayName("16. Una linea sin ':' se rechaza en esa linea")
    void lineWithoutSeparator() {
        assertRejected(LevelFormatRule.HEADER_MALFORMED_LINE, 1, "nombre Nivel 1", "tiempo: 90", "objetivo: 500");
    }

    @Test
    @DisplayName("17. Un encabezado vacio se rechaza por el primer campo que falta")
    void emptyHeader() {
        InvalidLevelException e = assertThrows(InvalidLevelException.class, () -> parse());

        assertEquals(LevelFormatRule.HEADER_MISSING_FIELD, e.getRule());
        assertEquals("nombre", e.getDetail());
    }
}
