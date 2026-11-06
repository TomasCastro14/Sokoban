package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AtomicFileWriterTest {

    @TempDir
    Path tempDir;

    private final AtomicFileWriter writer = new AtomicFileWriter();

    private List<String> filesInTempDir() throws IOException {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.map(file -> file.getFileName().toString()).toList();
        }
    }

    @Test
    @DisplayName("1. Reemplaza el contenido anterior por el nuevo")
    void replacesPreviousContent() throws IOException {
        Path target = tempDir.resolve("data.json");
        Files.writeString(target, "old", StandardCharsets.UTF_8);

        writer.write(target, out -> out.write("new"));

        assertEquals("new", Files.readString(target, StandardCharsets.UTF_8));
        assertEquals(List.of("data.json"), filesInTempDir());
    }

    @Test
    @DisplayName("2. Si la escritura falla a la mitad, el archivo anterior queda intacto y no quedan temporales")
    void failureHalfwayKeepsPreviousFile() throws IOException {
        Path target = tempDir.resolve("data.json");
        Files.writeString(target, "old", StandardCharsets.UTF_8);

        IOException failure = assertThrows(IOException.class, () -> writer.write(target, out -> {
            out.write("{ \"half\": ");
            throw new IOException("disk full");
        }));

        assertEquals("disk full", failure.getMessage());
        assertEquals("old", Files.readString(target, StandardCharsets.UTF_8));
        assertEquals(List.of("data.json"), filesInTempDir());
    }

    @Test
    @DisplayName("3. Escribe en UTF-8")
    void writesUtf8() throws IOException {
        Path target = tempDir.resolve("data.json");

        writer.write(target, out -> out.write("Ñandú"));

        assertEquals("Ñandú", Files.readString(target, StandardCharsets.UTF_8));
    }
}
