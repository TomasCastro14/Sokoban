package view.gameplay.dialog;

import model.game.loss.LossReason;
import model.game.score.GoalType;

import java.text.NumberFormat;
import java.util.Locale;

/** Every text the level popups show to the player, in one place. */
final class DialogTexts {
    static final String GOALS_TITLE = "Objetivos";
    static final String DEFEAT_TITLE = "¡Perdiste!";
    static final String VICTORY_TITLE = "¡Nivel completado!";
    static final String SUMMARY_TITLE = "Resumen";

    private static final String POINTS_SUFFIX = "pts";
    private static final String RESULT_SEPARATOR = " / ";
    private static final NumberFormat NUMBERS = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-AR"));

    private DialogTexts() {
    }

    static String nameOf(GoalType type) {
        return switch (type) {
            case MOVES -> "Movimientos";
            case PUSHES -> "Empujes";
            case UNDOS -> "Deshacer";
        };
    }

    static String messageFor(LossReason reason) {
        return switch (reason) {
            case DEADLOCK -> "Una caja quedó trabada y no te quedan usos de deshacer.";
            case TIME_OUT -> "Se te acabó el tiempo.";
            case BROKEN_BOX -> "Rompiste una caja frágil.";
            case WRONG_STORAGE -> "Acomodaste una caja en un destino que no es el suyo.";
            case EARLY_KEY_STORAGE -> "Guardaste la caja llave antes de abrir su puerta.";
        };
    }

    static String points(int score) {
        return NUMBERS.format(score) + POINTS_SUFFIX;
    }

    static String result(int achieved, int limit) {
        return achieved + RESULT_SEPARATOR + limit;
    }
}
