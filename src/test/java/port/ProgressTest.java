package port;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgressTest {

    private static final int LEVEL_COUNT = 5;

    @Test
    @DisplayName("1. El progreso inicial tiene el nivel 1 desbloqueado y ningun nivel ganado")
    void initialProgress() {
        Progress progress = Progress.initial();

        assertEquals(1, progress.unlockedLevel());
        assertTrue(progress.levels().isEmpty());
    }

    @Test
    @DisplayName("2. Ganar un nivel desbloquea el siguiente y guarda su resultado")
    void winningUnlocksNextLevel() {
        LevelRecord won = new LevelRecord(1, 20, 4);

        Progress progress = Progress.initial().withLevelWon(won, LEVEL_COUNT);

        assertEquals(2, progress.unlockedLevel());
        assertEquals(List.of(won), progress.levels());
    }

    @Test
    @DisplayName("3. Ganar el ultimo nivel no desbloquea uno que no existe")
    void lastLevelUnlocksNothingMore() {
        Progress progress = new Progress(LEVEL_COUNT, List.of())
                .withLevelWon(new LevelRecord(LEVEL_COUNT, 50, 10), LEVEL_COUNT);

        assertEquals(LEVEL_COUNT, progress.unlockedLevel());
    }

    @Test
    @DisplayName("4. Volver a ganar un nivel anterior no baja el ultimo desbloqueado")
    void replayingEarlierLevelKeepsUnlocked() {
        Progress progress = new Progress(4, List.of())
                .withLevelWon(new LevelRecord(1, 20, 4), LEVEL_COUNT);

        assertEquals(4, progress.unlockedLevel());
    }

    @Test
    @DisplayName("5. Se queda con el mejor resultado de cada nivel")
    void keepsBestRecord() {
        LevelRecord best = new LevelRecord(1, 18, 4);

        Progress progress = Progress.initial()
                .withLevelWon(best, LEVEL_COUNT)
                .withLevelWon(new LevelRecord(1, 30, 4), LEVEL_COUNT);

        assertEquals(List.of(best), progress.levels());
    }

    @Test
    @DisplayName("6. Es inmutable: cambiar la lista original no cambia el progreso")
    void isImmutable() {
        List<LevelRecord> levels = new ArrayList<>(List.of(new LevelRecord(1, 20, 4)));
        Progress progress = new Progress(2, levels);

        levels.clear();

        assertEquals(1, progress.levels().size());
        assertThrows(UnsupportedOperationException.class, () -> progress.levels().clear());
    }

    @Test
    @DisplayName("7. Rechaza un ultimo nivel desbloqueado menor a 1")
    void rejectsLastUnlockedBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> new Progress(0, List.of()));
    }

    @Test
    @DisplayName("8. Rechaza un nivel ganado que todavia no estaba desbloqueado")
    void rejectsWonLevelNotUnlocked() {
        List<LevelRecord> levels = List.of(new LevelRecord(3, 20, 4));

        assertThrows(IllegalArgumentException.class, () -> new Progress(2, levels));
    }

    @Test
    @DisplayName("9. Rechaza el mismo nivel repetido")
    void rejectsRepeatedLevel() {
        List<LevelRecord> levels = List.of(new LevelRecord(1, 20, 4), new LevelRecord(1, 25, 4));

        assertThrows(IllegalArgumentException.class, () -> new Progress(2, levels));
    }

    @Test
    @DisplayName("10. Rechaza una lista de niveles nula")
    void rejectsNullLevels() {
        assertThrows(NullPointerException.class, () -> new Progress(1, null));
    }
}
