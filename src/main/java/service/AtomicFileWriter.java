package service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Replaces a file all at once: the content goes to a temporary file in the same folder, which
 * then takes the place of the target. If writing fails or the program stops halfway, the target
 * keeps its previous content, and the temporary file never stays behind.
 */
final class AtomicFileWriter {
    private static final String TEMP_SUFFIX = ".tmp";

    @FunctionalInterface
    interface Content {
        void writeTo(Writer writer) throws IOException;
    }

    void write(Path target, Content content) throws IOException {
        Path folder = target.toAbsolutePath().getParent();
        Files.createDirectories(folder);
        Path temp = Files.createTempFile(folder, target.getFileName().toString(), TEMP_SUFFIX);
        try {
            writeTemp(temp, content);
            replace(temp, target);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static void writeTemp(Path temp, Content content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            content.writeTo(writer);
        }
    }

    /** Some file systems cannot move atomically; a plain replace is the best they offer. */
    private static void replace(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}