package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import model.game.LevelHeader;
import model.game.score.LevelGoals;
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
    @DisplayName("1. Un encabezado valido da nombre, tiempo (Duration) y las tres metas ya tipadas")
    void validHeaderIsTyped() throws InvalidLevelException {
        LevelHeader header = parse("nombre: Nivel 1", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1");

        assertEquals("Nivel 1", header.name());
        assertEquals(Duration.ofSeconds(90), header.timeLimit());
        assertEquals(new LevelGoals(60, 20, 1), header.goals());
    }

    @Test
    @DisplayName("2. El nombre conserva espacios, tildes y ñ")
    void nameKeepsAccentsAndSpaces() throws InvalidLevelException {
        assertEquals("El año del ñandú", parse("nombre: El año del ñandú", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1").name());
    }

    @Test
    @DisplayName("3. Se ignoran los espacios alrededor de clave y valor, y las lineas vacias")
    void extraSpacesAndBlankLinesAreIgnored() throws InvalidLevelException {
        LevelHeader header = parse("  nombre  :   Nivel 1  ", "", "tiempo:90", "   ",
                " movimientos : 60 ", "empujes:20", "undo :1");

        assertEquals("Nivel 1", header.name());
        assertEquals(Duration.ofSeconds(90), header.timeLimit());
        assertEquals(new LevelGoals(60, 20, 1), header.goals());
    }

    @Test
    @DisplayName("4. Los campos pueden venir en cualquier orden")
    void fieldsInAnyOrder() throws InvalidLevelException {
        LevelHeader header = parse("undo: 1", "empujes: 20", "movimientos: 60", "nombre: Nivel 1", "tiempo: 90");

        assertEquals("Nivel 1", header.name());
        assertEquals(new LevelGoals(60, 20, 1), header.goals());
    }

    @Test
    @DisplayName("5. Si falta el nombre se rechaza (regla global, sin linea)")
    void missingName() {
        assertRejected(LevelFormatRule.HEADER_MISSING_FIELD, InvalidLevelException.NO_LINE,
                "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("6. Si falta el tiempo se rechaza")
    void missingTime() {
        assertRejected(LevelFormatRule.HEADER_MISSING_FIELD, InvalidLevelException.NO_LINE,
                "nombre: N", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("7. Si falta alguna de las metas se rechaza, nombrando la que falta")
    void missingGoal() throws Exception {
        assertMissing("movimientos", "nombre: N", "tiempo: 90", "empujes: 20", "undo: 1");
        assertMissing("empujes", "nombre: N", "tiempo: 90", "movimientos: 60", "undo: 1");
        assertMissing("undo", "nombre: N", "tiempo: 90", "movimientos: 60", "empujes: 20");
    }

    private void assertMissing(String field, String... lines) {
        InvalidLevelException e = assertThrows(InvalidLevelException.class, () -> parse(lines));
        assertEquals(LevelFormatRule.HEADER_MISSING_FIELD, e.getRule());
        assertEquals(field, e.getDetail());
    }

    @Test
    @DisplayName("8. Un campo repetido se rechaza en la linea de la repeticion")
    void duplicateField() {
        assertRejected(LevelFormatRule.HEADER_DUPLICATE_FIELD, 3,
                "nombre: N", "tiempo: 90", "tiempo: 60", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("9. Una clave desconocida se rechaza en su linea (el viejo 'objetivo' tambien)")
    void unknownField() {
        assertRejected(LevelFormatRule.HEADER_UNKNOWN_FIELD, 2,
                "nombre: N", "autor: Ana", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1");
        assertRejected(LevelFormatRule.HEADER_UNKNOWN_FIELD, 3, "nombre: N", "tiempo: 90", "objetivo: 500");
    }

    @Test
    @DisplayName("10. Las claves distinguen mayusculas: 'Nombre' es desconocida")
    void keysAreCaseSensitive() {
        assertRejected(LevelFormatRule.HEADER_UNKNOWN_FIELD, 1,
                "Nombre: N", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("11. Un tiempo no numerico se rechaza en la linea del tiempo")
    void timeNotNumeric() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 2,
                "nombre: N", "tiempo: abc", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("12. Un tiempo negativo o cero se rechaza en la linea del tiempo")
    void timeNotPositive() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 2,
                "nombre: N", "tiempo: -5", "movimientos: 60", "empujes: 20", "undo: 1");
        assertRejected(LevelFormatRule.INVALID_HEADER, 2,
                "nombre: N", "tiempo: 0", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("13. Un tiempo con formato mm:ss no es valido (son segundos enteros)")
    void timeWithMinutesFormat() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 2,
                "nombre: N", "tiempo: 1:30", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("14. Una meta invalida (texto o negativa) se rechaza en su linea")
    void invalidGoal() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 3,
                "nombre: N", "tiempo: 90", "movimientos: muchos", "empujes: 20", "undo: 1");
        assertRejected(LevelFormatRule.INVALID_HEADER, 4,
                "nombre: N", "tiempo: 90", "movimientos: 60", "empujes: -1", "undo: 1");
        assertRejected(LevelFormatRule.INVALID_HEADER, 5,
                "nombre: N", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: x");
    }

    @Test
    @DisplayName("15. Un nombre vacio se rechaza en la linea del nombre")
    void emptyName() {
        assertRejected(LevelFormatRule.INVALID_HEADER, 1,
                "nombre:   ", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("16. Una linea sin ':' se rechaza en esa linea")
    void lineWithoutSeparator() {
        assertRejected(LevelFormatRule.HEADER_MALFORMED_LINE, 1,
                "nombre Nivel 1", "tiempo: 90", "movimientos: 60", "empujes: 20", "undo: 1");
    }

    @Test
    @DisplayName("17. Un encabezado vacio se rechaza por el primer campo que falta")
    void emptyHeader() {
        InvalidLevelException e = assertThrows(InvalidLevelException.class, () -> parse());

        assertEquals(LevelFormatRule.HEADER_MISSING_FIELD, e.getRule());
        assertEquals("nombre", e.getDetail());
    }
}
