package exception;

/**
 * Every format rule a level file can break. A new validation adds a value here;
 * the exception itself never changes.
 */
public enum LevelFormatRule {
    MISSING_SEPARATOR("falta la línea --- entre el encabezado y el mapa"),
    INVALID_HEADER("el encabezado tiene un valor inválido"),
    UNKNOWN_CHARACTER("hay un carácter desconocido en el mapa"),
    OPEN_BORDER("el borde del mapa tiene que ser # o %"),
    PLAYER_COUNT("tiene que haber exactamente un Sokoban"),
    BOX_STORAGE_MISMATCH("cada tipo de caja necesita tantos puntos de guardado de su color");

    private final String description;

    LevelFormatRule(String description) {
        this.description = description;
    }

    /** Readable text shown to the player. */
    public String description() {
        return description;
    }
}
