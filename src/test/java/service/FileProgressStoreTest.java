package service;

import model.game.progress.Progress;
import model.game.progress.ProgressData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

class FileProgressStoreTest {

    @TempDir
    Path dir;

    @Test
    @DisplayName("1. Sin archivo guardado no hay progreso que cargar")
    void noFileMeansNoProgress() {
        assertEquals(Optional.empty(), new FileProgressStore(dir.resolve("progress.properties")).load());
    }

    @Test
    @DisplayName("2. Lo que se guarda se vuelve a leer igual (crea la carpeta si no existe)")
    void saveThenLoad() {
        FileProgressStore store = new FileProgressStore(dir.resolve("sub/progress.properties"));
        ProgressData data = new ProgressData(3, Map.of(1, 3, 2, 1), Map.of(1, 1500, 2, 700));

        store.save(data);

        assertEquals(Optional.of(data), store.load());
    }

    @Test
    @DisplayName("3. Simula cerrar y volver a abrir el juego: el progreso sobrevive")
    void progressSurvivesARestart() {
        Path file = dir.resolve("progress.properties");
        Progress firstRun = new Progress(5);
        controller.ProgressAutosave.attach(firstRun, new FileProgressStore(file));
        firstRun.registerResult(1, 2, 900);
        firstRun.registerResult(2, 3, 1200);

        Progress secondRun = new Progress(5);
        controller.ProgressAutosave.attach(secondRun, new FileProgressStore(file));

        assertEquals(3, secondRun.getLastUnlockedLevel());
        assertEquals(2, secondRun.getStars(1));
        assertEquals(OptionalInt.of(1200), secondRun.getBestScore(2));
    }

    @Test
    @DisplayName("4. Un archivo roto no rompe el juego: se ignora lo invalido")
    void brokenFileIsTolerated() throws IOException {
        Path file = dir.resolve("progress.properties");
        Files.writeString(file, """
                lastUnlockedLevel=abc
                level.1.stars=2
                level.1.score=500
                level.x.score=10
                otra.cosa=1
                """, StandardCharsets.UTF_8);

        ProgressData data = new FileProgressStore(file).load().orElseThrow();

        assertEquals(1, data.lastUnlockedLevel());
        assertEquals(Map.of(1, 2), data.starsByLevel());
        assertEquals(Map.of(1, 500), data.bestScoreByLevel());
    }

    @Test
    @DisplayName("5. Guardar sobre un archivo existente lo reemplaza")
    void saveReplaces() {
        FileProgressStore store = new FileProgressStore(dir.resolve("progress.properties"));
        store.save(new ProgressData(4, Map.of(1, 3), Map.of(1, 100)));

        ProgressData reset = new ProgressData(1, Map.of(), Map.of());
        store.save(reset);

        assertEquals(Optional.of(reset), store.load());
    }
}
