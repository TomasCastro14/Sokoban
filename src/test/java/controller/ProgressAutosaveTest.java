package controller;

import model.game.progress.Progress;
import model.game.progress.ProgressData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import port.ProgressStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProgressAutosaveTest {

    /** Remembers in memory what would have been written to disk. */
    private static final class MemoryStore implements ProgressStore {
        private final Optional<ProgressData> initial;
        final List<ProgressData> saved = new ArrayList<>();

        MemoryStore(Optional<ProgressData> initial) {
            this.initial = initial;
        }

        @Override public Optional<ProgressData> load() { return initial; }
        @Override public void save(ProgressData data) { saved.add(data); }
    }

    @Test
    @DisplayName("1. Al arrancar carga lo guardado sin volver a escribirlo")
    void loadsOnAttach() {
        MemoryStore store = new MemoryStore(Optional.of(new ProgressData(2, Map.of(1, 3), Map.of(1, 100))));
        Progress progress = new Progress(5);

        ProgressAutosave.attach(progress, store);

        assertEquals(2, progress.getLastUnlockedLevel());
        assertEquals(3, progress.getStars(1));
        assertTrue(store.saved.isEmpty());
    }

    @Test
    @DisplayName("2. Guarda al ganar un nivel y al reiniciar el progreso")
    void savesOnEveryChange() {
        MemoryStore store = new MemoryStore(Optional.empty());
        Progress progress = new Progress(5);
        ProgressAutosave.attach(progress, store);

        progress.registerResult(1, 2, 500);
        progress.reset();

        assertEquals(2, store.saved.size());
        assertEquals(2, store.saved.get(0).lastUnlockedLevel());
        assertEquals(new ProgressData(1, Map.of(), Map.of()), store.saved.get(1));
    }
}
