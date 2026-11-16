package service;

import model.game.progress.ProgressData;
import port.ProgressStore;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Pure Fabrication: keeps the progress in a small text file (.properties) on the player's computer,
 * no database involved. By default it lives in ~/.sokoban/progress.properties, outside the project
 * and the .jar, so it survives closing the game and rebuilding it.
 *
 * File format (one line per value, human readable):
 * <pre>
 * lastUnlockedLevel=3
 * level.1.stars=2
 * level.1.score=1500
 * </pre>
 * A missing or broken file is never fatal: the game just starts from scratch.
 */
public final class FileProgressStore implements ProgressStore {
    private static final Logger LOG = System.getLogger(FileProgressStore.class.getName());
    private static final String LAST_UNLOCKED = "lastUnlockedLevel";
    private static final String LEVEL_PREFIX = "level.";
    private static final String STARS_SUFFIX = ".stars";
    private static final String SCORE_SUFFIX = ".score";
    private static final String HEADER = "Sokoban - progreso guardado";

    private final Path file;

    /** @param file where to read and write the progress; its folder is created when saving */
    public FileProgressStore(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    /** ~/.sokoban/progress.properties */
    public static FileProgressStore inUserHome() {
        return new FileProgressStore(Path.of(System.getProperty("user.home"), ".sokoban", "progress.properties"));
    }

    public Path getFile() {
        return file;
    }

    @Override
    public Optional<ProgressData> load() {
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try (Reader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(in);
            return Optional.of(toData(props));
        } catch (IOException | IllegalArgumentException e) {
            LOG.log(Logger.Level.WARNING, "Could not read the saved progress at " + file + ", starting from scratch", e);
            return Optional.empty();
        }
    }

    /** Writes to a temporary file first and then replaces the old one, so a crash never leaves it half written. */
    @Override
    public void save(ProgressData data) {
        Objects.requireNonNull(data, "data");
        try {
            Path folder = file.toAbsolutePath().getParent();
            Files.createDirectories(folder);
            Path temp = Files.createTempFile(folder, "progress", ".tmp");
            try (Writer out = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                toProperties(data).store(out, HEADER);
            }
            moveReplacing(temp, file);
        } catch (IOException e) {
            LOG.log(Logger.Level.WARNING, "Could not save the progress to " + file, e);
        }
    }

    private static void moveReplacing(Path from, Path to) throws IOException {
        try {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicNotSupported) {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Properties toProperties(ProgressData data) {
        Properties props = new Properties();
        props.setProperty(LAST_UNLOCKED, Integer.toString(data.lastUnlockedLevel()));
        data.bestScoreByLevel().forEach((level, score) -> {
            props.setProperty(LEVEL_PREFIX + level + SCORE_SUFFIX, Integer.toString(score));
            props.setProperty(LEVEL_PREFIX + level + STARS_SUFFIX,
                    Integer.toString(data.starsByLevel().getOrDefault(level, 0)));
        });
        return props;
    }

    /** Unknown keys and values that are not numbers are skipped; Progress.restore checks the ranges. */
    private static ProgressData toData(Properties props) {
        int lastUnlocked = parse(props.getProperty(LAST_UNLOCKED)).orElse(1);
        Map<Integer, Integer> stars = new HashMap<>();
        Map<Integer, Integer> scores = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (!key.startsWith(LEVEL_PREFIX)) {
                continue;
            }
            String rest = key.substring(LEVEL_PREFIX.length());
            Optional<Integer> value = parse(props.getProperty(key));
            if (rest.endsWith(STARS_SUFFIX)) {
                levelOf(rest, STARS_SUFFIX).ifPresent(level -> value.ifPresent(v -> stars.put(level, v)));
            } else if (rest.endsWith(SCORE_SUFFIX)) {
                levelOf(rest, SCORE_SUFFIX).ifPresent(level -> value.ifPresent(v -> scores.put(level, v)));
            }
        }
        return new ProgressData(lastUnlocked, stars, scores);
    }

    private static Optional<Integer> levelOf(String rest, String suffix) {
        return parse(rest.substring(0, rest.length() - suffix.length()));
    }

    private static Optional<Integer> parse(String text) {
        if (text == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(text.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
