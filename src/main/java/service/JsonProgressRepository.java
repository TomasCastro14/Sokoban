package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import port.LevelRecord;
import port.Progress;
import port.ProgressRepository;

import java.io.IOException;
import java.io.Reader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;


public final class JsonProgressRepository implements ProgressRepository {
    private static final Logger LOG = System.getLogger(JsonProgressRepository.class.getName());
    private static final String CORRUPT_SUFFIX = ".corrupt-";

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final AtomicFileWriter writer = new AtomicFileWriter();

    public JsonProgressRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public Progress load() {
        if (Files.notExists(file)) {
            return Progress.initial();
        }
        try {
            return read().toProgress();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not read the progress in " + file + "; starting from scratch", e);
        } catch (JsonParseException | IllegalArgumentException e) {
            LOG.log(Level.WARNING, "The progress in " + file + " is not valid; starting from scratch", e);
            setAsideCorrupt();
        }
        return Progress.initial();
    }

    @Override
    public void save(Progress progress) {
        ProgressJson json = ProgressJson.from(progress);
        try {
            writer.write(file, out -> gson.toJson(json, out));
        } catch (IOException | JsonIOException e) {
            LOG.log(Level.WARNING, "Could not save the progress in " + file + "; the game goes on", e);
        }
    }

    private ProgressJson read() throws IOException {
        try (Reader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            ProgressJson json = gson.fromJson(in, ProgressJson.class);
            if (json == null) {
                throw new JsonParseException("The file is empty");
            }
            return json;
        }
    }

    private void setAsideCorrupt() {
        Path backup = file.resolveSibling(file.getFileName() + CORRUPT_SUFFIX + System.currentTimeMillis());
        try {
            Files.move(file, backup);
            LOG.log(Level.WARNING, "The invalid progress was kept as " + backup);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not keep the invalid progress as " + backup, e);
        }
    }

    
    private record ProgressJson(Integer lastUnlockedLevel, List<LevelJson> levels) {

        static ProgressJson from(Progress progress) {
            return new ProgressJson(progress.lastUnlockedLevel(),
                    progress.levels().stream().map(LevelJson::from).toList());
        }

        Progress toProgress() {
            if (levels == null || levels.contains(null)) {
                throw new IllegalArgumentException("The list of levels is missing or has empty entries");
            }
            return new Progress(required(lastUnlockedLevel, "lastUnlockedLevel"),
                    levels.stream().map(LevelJson::toRecord).toList());
        }
    }

    private record LevelJson(Integer levelNumber, Integer fewestMoves, Integer fewestPushes) {

        static LevelJson from(LevelRecord record) {
            return new LevelJson(record.levelNumber(), record.fewestMoves(), record.fewestPushes());
        }

        LevelRecord toRecord() {
            return new LevelRecord(required(levelNumber, "levelNumber"),
                    required(fewestMoves, "fewestMoves"), required(fewestPushes, "fewestPushes"));
        }
    }

    private static int required(Integer value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Missing field " + field);
        }
        return value;
    }
}