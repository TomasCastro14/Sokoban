package model.game.progress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

class ProgressTest {

    private static final int LEVEL_COUNT = 5;
    private static final int SCORE = 1_000;

    private final Progress progress = new Progress(LEVEL_COUNT);

    @Test
    @DisplayName("1. Estado inicial: solo el nivel 1 desbloqueado y 0 estrellas en todos")
    void initialState() {
        assertEquals(1, progress.getLastUnlockedLevel());
        assertTrue(progress.isUnlocked(1));
        for (int level = 1; level <= LEVEL_COUNT; level++) {
            assertEquals(0, progress.getStars(level));
            assertEquals(OptionalInt.empty(), progress.getBestScore(level));
        }
        for (int level = 2; level <= LEVEL_COUNT; level++) {
            assertFalse(progress.isUnlocked(level));
        }
    }

    @Test
    @DisplayName("2. Cada instancia nueva arranca de cero: no hereda nada de otra")
    void everyInstanceStartsFromScratch() {
        progress.registerResult(1, 3, SCORE);

        Progress nextRun = new Progress(LEVEL_COUNT);

        assertEquals(1, nextRun.getLastUnlockedLevel());
        assertEquals(0, nextRun.getStars(1));
    }

    @Test
    @DisplayName("3. Ganar un nivel desbloquea el siguiente y guarda sus estrellas")
    void winningUnlocksTheNextLevel() {
        progress.registerResult(1, 2, SCORE);

        assertTrue(progress.isUnlocked(2));
        assertFalse(progress.isUnlocked(3));
        assertEquals(2, progress.getLastUnlockedLevel());
        assertEquals(2, progress.getStars(1));
        assertEquals(OptionalInt.of(SCORE), progress.getBestScore(1));
    }

    @Test
    @DisplayName("4. Ganar con 0 estrellas igual desbloquea el siguiente")
    void zeroStarsStillUnlocks() {
        progress.registerResult(1, 0, 0);

        assertTrue(progress.isUnlocked(2));
        assertEquals(0, progress.getStars(1));
    }

    @Test
    @DisplayName("5. Ganar el ultimo nivel no desbloquea nada fuera de rango")
    void lastLevelDoesNotGoOutOfRange() {
        for (int level = 1; level <= LEVEL_COUNT; level++) {
            progress.registerResult(level, 1, SCORE);
        }

        assertEquals(LEVEL_COUNT, progress.getLastUnlockedLevel());
    }

    @Test
    @DisplayName("6. Volver a ganar un nivel anterior no bloquea los que ya estaban desbloqueados")
    void replayingAnEarlierLevelKeepsUnlocks() {
        progress.registerResult(1, 1, SCORE);
        progress.registerResult(2, 1, SCORE);

        progress.registerResult(1, 3, SCORE);

        assertEquals(3, progress.getLastUnlockedLevel());
    }

    @Test
    @DisplayName("7. Solo se actualiza si mejora el record: un peor resultado no baja estrellas ni puntaje")
    void onlyBetterResultsAreKept() {
        assertTrue(progress.registerResult(1, 2, SCORE), "la primera victoria es record");

        assertFalse(progress.registerResult(1, 1, SCORE - 1));
        assertEquals(2, progress.getStars(1));
        assertEquals(OptionalInt.of(SCORE), progress.getBestScore(1));

        assertFalse(progress.registerResult(1, 2, SCORE), "igualar no es superar");
    }

    @Test
    @DisplayName("8. Mas estrellas o mas puntos es record, y cada maximo se guarda por separado")
    void moreStarsOrMorePointsIsARecord() {
        progress.registerResult(1, 1, SCORE);

        assertTrue(progress.registerResult(1, 2, SCORE - 500), "mas estrellas");
        assertEquals(2, progress.getStars(1));
        assertEquals(OptionalInt.of(SCORE), progress.getBestScore(1));

        assertTrue(progress.registerResult(1, 1, SCORE + 500), "mas puntos");
        assertEquals(2, progress.getStars(1));
        assertEquals(OptionalInt.of(SCORE + 500), progress.getBestScore(1));
    }

    @ParameterizedTest(name = "{0} estrellas es valido")
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("9. Limites validos de estrellas: 0 a 3")
    void starsWithinLimits(int stars) {
        progress.registerResult(1, stars, SCORE);

        assertEquals(stars, progress.getStars(1));
    }

    @ParameterizedTest(name = "{0} estrellas se rechaza")
    @ValueSource(ints = {-1, 4})
    @DisplayName("10. Fuera de 0..3 se rechaza y no cambia nada")
    void starsOutOfLimitsAreRejected(int stars) {
        assertThrows(IllegalArgumentException.class, () -> progress.registerResult(1, stars, SCORE));

        assertEquals(0, progress.getStars(1));
        assertFalse(progress.isUnlocked(2));
    }

    @Test
    @DisplayName("11. Un puntaje negativo se rechaza")
    void negativeScoreIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> progress.registerResult(1, 1, -1));
    }

    @ParameterizedTest(name = "nivel {0}")
    @ValueSource(ints = {0, LEVEL_COUNT + 1})
    @DisplayName("12. Un nivel fuera de rango se rechaza")
    void levelOutOfRangeIsRejected(int level) {
        assertThrows(IllegalArgumentException.class, () -> progress.isUnlocked(level));
        assertThrows(IllegalArgumentException.class, () -> progress.getStars(level));
        assertThrows(IllegalArgumentException.class, () -> progress.registerResult(level, 1, SCORE));
    }

    @Test
    @DisplayName("13. No se puede registrar una victoria en un nivel bloqueado")
    void lockedLevelCannotBeWon() {
        assertThrows(IllegalStateException.class, () -> progress.registerResult(3, 3, SCORE));

        assertEquals(0, progress.getStars(3));
    }

    @Test
    @DisplayName("14. Reiniciar vuelve al estado inicial")
    void resetGoesBackToTheStart() {
        progress.registerResult(1, 3, SCORE);
        progress.registerResult(2, 2, SCORE);

        progress.reset();

        assertEquals(1, progress.getLastUnlockedLevel());
        assertFalse(progress.isUnlocked(2));
        assertEquals(0, progress.getStars(1));
        assertEquals(OptionalInt.empty(), progress.getBestScore(1));
        assertTrue(progress.registerResult(1, 1, 1), "despues de reiniciar, la primera victoria vuelve a ser record");
    }

    @Test
    @DisplayName("15. Avisa a sus observadores al ganar y al reiniciar")
    void listenersAreNotified() {
        List<Progress> notified = new ArrayList<>();
        progress.addListener(notified::add);

        progress.registerResult(1, 1, SCORE);
        progress.reset();

        assertEquals(List.of(progress, progress), notified);
    }

    @Test
    @DisplayName("16. Un juego sin niveles se rechaza")
    void needsAtLeastOneLevel() {
        assertThrows(IllegalArgumentException.class, () -> new Progress(0));
    }

    @Test
    @DisplayName("17. Una foto del progreso restaurada en otra instancia da el mismo progreso")
    void snapshotAndRestore() {
        progress.registerResult(1, 3, SCORE);
        progress.registerResult(2, 1, SCORE + 1);

        Progress nextRun = new Progress(LEVEL_COUNT);
        nextRun.restore(progress.snapshot());

        assertEquals(progress.snapshot(), nextRun.snapshot());
        assertEquals(3, nextRun.getLastUnlockedLevel());
        assertEquals(3, nextRun.getStars(1));
        assertEquals(OptionalInt.of(SCORE + 1), nextRun.getBestScore(2));
    }

    @Test
    @DisplayName("18. Restaurar datos invalidos ignora lo que esta fuera de rango")
    void restoreIsLenient() {
        progress.restore(new ProgressData(99,
                Map.of(1, 7, 2, 2, 9, 3),
                Map.of(1, SCORE, 2, -5, 9, SCORE)));

        assertEquals(LEVEL_COUNT, progress.getLastUnlockedLevel());
        assertEquals(0, progress.getStars(1), "estrellas invalidas cuentan como 0");
        assertEquals(OptionalInt.of(SCORE), progress.getBestScore(1));
        assertEquals(OptionalInt.empty(), progress.getBestScore(2), "puntaje negativo se ignora");
    }

    @Test
    @DisplayName("19. Un nivel ganado deja desbloqueado el siguiente aunque el dato guardado diga otra cosa")
    void restoreKeepsWonLevelsConsistent() {
        progress.restore(new ProgressData(1, Map.of(2, 1), Map.of(2, SCORE)));

        assertEquals(3, progress.getLastUnlockedLevel());
    }

    @Test
    @DisplayName("20. Restaurar avisa a los observadores")
    void restoreNotifies() {
        List<Progress> notified = new ArrayList<>();
        progress.addListener(notified::add);

        progress.restore(new ProgressData(1, Map.of(), Map.of()));

        assertEquals(List.of(progress), notified);
    }
}
