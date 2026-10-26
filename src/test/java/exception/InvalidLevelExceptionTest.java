package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidLevelExceptionTest {

    /** Model of how a validation throws the exception. */
    private static void validationThatFindsTwoPlayersAtLine7() throws InvalidLevelException {
        throw new InvalidLevelException(LevelFormatRule.PLAYER_COUNT, 7);
    }

    @Test
    @DisplayName("1. Con regla y linea, getRule y getLine devuelven exactamente esos valores")
    void keepsRuleAndLine() {
        InvalidLevelException e = new InvalidLevelException(LevelFormatRule.OPEN_BORDER, 5);

        assertEquals(LevelFormatRule.OPEN_BORDER, e.getRule());
        assertEquals(5, e.getLine());
        assertTrue(e.hasLine());
    }

    @Test
    @DisplayName("2. El mensaje tiene la descripcion legible de la regla y el numero de linea")
    void messageHasDescriptionAndLine() {
        InvalidLevelException e = new InvalidLevelException(LevelFormatRule.PLAYER_COUNT, 7);

        assertEquals("Nivel inválido: " + LevelFormatRule.PLAYER_COUNT.description() + " (línea 7)", e.getMessage());
    }

    @Test
    @DisplayName("3. El detalle opcional aparece en el mensaje despues de la regla")
    void messageHasOptionalDetail() {
        InvalidLevelException e = new InvalidLevelException(LevelFormatRule.UNKNOWN_CHARACTER, 6, "carácter 'X'");

        assertEquals("carácter 'X'", e.getDetail());
        assertEquals("Nivel inválido: " + LevelFormatRule.UNKNOWN_CHARACTER.description()
                + ": carácter 'X' (línea 6)", e.getMessage());
    }

    @Test
    @DisplayName("4. Una regla global (sin linea) no muestra una linea falsa en el mensaje")
    void globalRuleHasNoLine() {
        InvalidLevelException e = new InvalidLevelException(LevelFormatRule.PLAYER_COUNT);

        assertFalse(e.hasLine());
        assertEquals(InvalidLevelException.NO_LINE, e.getLine());
        assertFalse(e.getMessage().contains("línea"), e.getMessage());
        assertEquals("Nivel inválido: " + LevelFormatRule.PLAYER_COUNT.description(), e.getMessage());
    }

    @Test
    @DisplayName("5. Una regla global tambien puede llevar detalle")
    void globalRuleWithDetail() {
        InvalidLevelException e = new InvalidLevelException(LevelFormatRule.BOX_STORAGE_MISMATCH, "NORMAL: 2 cajas y 1 punto");

        assertFalse(e.hasLine());
        assertEquals("Nivel inválido: " + LevelFormatRule.BOX_STORAGE_MISMATCH.description()
                + ": NORMAL: 2 cajas y 1 punto", e.getMessage());
    }

    @Test
    @DisplayName("6. No se puede crear la excepcion sin regla")
    void ruleIsRequired() {
        assertThrows(NullPointerException.class, () -> new InvalidLevelException((LevelFormatRule) null, 3));
        assertThrows(NullPointerException.class, () -> new InvalidLevelException((LevelFormatRule) null));
    }

    @Test
    @DisplayName("7. Las lineas se cuentan desde 1: 0 o negativa falla al construir")
    void lineMustStartAtOne() {
        assertThrows(IllegalArgumentException.class, () -> new InvalidLevelException(LevelFormatRule.OPEN_BORDER, 0));
        assertThrows(IllegalArgumentException.class, () -> new InvalidLevelException(LevelFormatRule.OPEN_BORDER, -4));
        assertThrows(IllegalArgumentException.class,
                () -> new InvalidLevelException(LevelFormatRule.OPEN_BORDER, InvalidLevelException.NO_LINE),
                "a global rule uses the constructor without line, not NO_LINE");
    }

    @Test
    @DisplayName("8. Es checked y se atrapa con assertThrows verificando regla y linea (modelo para las validaciones)")
    void isCheckedAndCatchable() {
        assertFalse(RuntimeException.class.isAssignableFrom(InvalidLevelException.class));

        InvalidLevelException e = assertThrows(InvalidLevelException.class,
                InvalidLevelExceptionTest::validationThatFindsTwoPlayersAtLine7);

        assertEquals(LevelFormatRule.PLAYER_COUNT, e.getRule());
        assertEquals(7, e.getLine());
    }

    @Test
    @DisplayName("9. Cada regla del enum tiene una descripcion no vacia")
    void everyRuleHasDescription() {
        for (LevelFormatRule rule : LevelFormatRule.values()) {
            assertFalse(rule.description().isBlank(), rule.name());
        }
    }

    @Test
    @DisplayName("10. El constructor viejo (texto, linea) sigue andando mientras migran las validaciones")
    @SuppressWarnings("deprecation")
    void legacyConstructorStillWorks() {
        InvalidLevelException e = new InvalidLevelException("Missing '---'", InvalidLevelException.NO_LINE);

        assertEquals("Missing '---'", e.getMessage());
        assertFalse(e.hasLine());
        assertNull(e.getRule());
    }
}
