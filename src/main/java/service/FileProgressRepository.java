package service;

import port.Progress;
import port.ProgressRepository;

import java.nio.file.Path;

public class FileProgressRepository implements ProgressRepository {
    private final Path file;

    public FileProgressRepository(Path file) {
        this.file = file;
    }

    @Override
    public void save(Progress progress) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Progress load() {
        throw new UnsupportedOperationException("TODO");
    }
}
