package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import port.LevelRecord;
import port.Progress;
import port.ProgressRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonProgressRepositoryTest {

    @TempDir
    Path tempDir;

    private Path progressFile() {
        return tempDir.resolve("progress.json");
    }

    private static Progress twoLevelsWon() {
        return new Progress(3, List.of(new LevelRecord(1, 20, 4), new LevelRecord(2, 35, 9)));
    }

    private List<String> filesInTempDir() throws IOException {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.map(file -> file.getFileName().toString()).sorted().toList();
        }
    }

    @Test
    @DisplayName("1. Guardar y volver a cargar devuelve los mismos LevelRecord")
    void savesAndLoadsSameRecords() {
        new JsonProgressRepository(progressFile()).save(twoLevelsWon());
        ProgressRepository reopened = new JsonProgressRepository(progressFile());

        assertEquals(twoLevelsWon(), reopened.load());
    }

    @Test
    @DisplayName("2. Si no existe el archivo, el progreso arranca en el nivel 1 sin excepcion")
    void missingFileStartsAtLevelOne() {
        ProgressRepository repository = new JsonProgressRepository(tempDir.resolve("missing.json"));

        Progress progress = assertDoesNotThrow(repository::load);

        assertEquals(Progress.initial(), progress);
    }

    @Test
    @DisplayName("3. Un JSON corrupto da progreso inicial, sin excepcion, y el archivo se conserva aparte")
    void corruptJsonIsKeptAside() throws IOException {
        String corrupt = "{ \"lastUnlockedLevel\": 3, \"levels\": [ {";
        Files.writeString(progressFile(), corrupt, StandardCharsets.UTF_8);

        Progress progress = assertDoesNotThrow(new JsonProgressRepository(progressFile())::load);

        assertEquals(Progress.initial(), progress);
        List<String> files = filesInTempDir();
        assertEquals(1, files.size());
        assertTrue(files.get(0).startsWith("progress.json.corrupt-"));
        assertEquals(corrupt, Files.readString(tempDir.resolve(files.get(0)), StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("4. Datos invalidos (nivel negativo) se tratan igual que un JSON corrupto")
    void invalidDataIsTreatedAsCorrupt() throws IOException {
        Files.writeString(progressFile(),
                "{\"lastUnlockedLevel\": 2, \"levels\": [{\"levelNumber\": -1, \"fewestMoves\": 5, \"fewestPushes\": 1}]}",
                StandardCharsets.UTF_8);

        assertEquals(Progress.initial(), new JsonProgressRepository(progressFile()).load());
        assertTrue(filesInTempDir().get(0).startsWith("progress.json.corrupt-"));
    }

    @Test
    @DisplayName("5. Un campo faltante o un archivo vacio dan progreso inicial")
    void missingFieldOrEmptyFileStartsFromScratch() throws IOException {
        Path missingField = tempDir.resolve("missing-field.json");
        Path empty = tempDir.resolve("empty.json");
        Files.writeString(missingField, "{\"levels\": []}", StandardCharsets.UTF_8);
        Files.writeString(empty, "", StandardCharsets.UTF_8);

        assertEquals(Progress.initial(), new JsonProgressRepository(missingField).load());
        assertEquals(Progress.initial(), new JsonProgressRepository(empty).load());
    }

    @Test
    @DisplayName("6. Guardar no deja archivos temporales en la carpeta")
    void saveLeavesNoTemporaryFiles() throws IOException {
        ProgressRepository repository = new JsonProgressRepository(progressFile());

        repository.save(Progress.initial());
        repository.save(twoLevelsWon());

        assertEquals(List.of("progress.json"), filesInTempDir());
    }

    @Test
    @DisplayName("7. Guardar crea la carpeta si no existe")
    void saveCreatesFolder() {
        Path nested = tempDir.resolve("sokoban").resolve("progress.json");

        new JsonProgressRepository(nested).save(twoLevelsWon());

        assertTrue(Files.exists(nested));
    }

    @Test
    @DisplayName("8. Si la escritura falla, no se lanza excepcion")
    void failedSaveDoesNotThrow() throws IOException {
        Path notAFolder = tempDir.resolve("not-a-folder");
        Files.writeString(notAFolder, "a file", StandardCharsets.UTF_8);
        ProgressRepository repository = new JsonProgressRepository(notAFolder.resolve("progress.json"));

        assertDoesNotThrow(() -> repository.save(twoLevelsWon()));
    }

    @Test
    @DisplayName("9. El JSON guardado tiene los campos esperados en UTF-8")
    void savedJsonHasExpectedFields() throws IOException {
        new JsonProgressRepository(progressFile()).save(twoLevelsWon());

        String json = Files.readString(progressFile(), StandardCharsets.UTF_8);

        assertTrue(json.contains("\"lastUnlockedLevel\": 3"));
        assertTrue(json.contains("\"levelNumber\": 2"));
        assertTrue(json.contains("\"fewestMoves\": 35"));
        assertTrue(json.contains("\"fewestPushes\": 9"));
    }
}