package port;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public record Progress(int lastUnlockedLevel, List<LevelRecord> levels) {

    static final int FIRST_LEVEL = 1;

    public Progress {
        if (lastUnlockedLevel < FIRST_LEVEL) {
            throw new IllegalArgumentException("The last unlocked level must be at least 1, got " + lastUnlockedLevel);
        }
        levels = List.copyOf(Objects.requireNonNull(levels, "levels"));
        requireUnlockedAndUnique(lastUnlockedLevel, levels);
    }

    /** First run: only the first level is unlocked and nothing has been won yet. */
    public static Progress initial() {
        return new Progress(FIRST_LEVEL, List.of());
    }

    /** Unlocks the next level (never past the last one) and keeps the best result of the level won. */
    public Progress withLevelWon(LevelRecord won, int levelCount) {
        int nextLevel = Math.min(won.levelNumber() + 1, levelCount);
        return new Progress(Math.max(lastUnlockedLevel, nextLevel), replaceRecord(won));
    }

    private List<LevelRecord> replaceRecord(LevelRecord won) {
        List<LevelRecord> updated = new ArrayList<>();
        LevelRecord best = won;
        for (LevelRecord record : levels) {
            if (record.levelNumber() == won.levelNumber()) {
                best = record.bestOf(won);
            } else {
                updated.add(record);
            }
        }
        updated.add(best);
        return updated;
    }

    private static void requireUnlockedAndUnique(int lastUnlockedLevel, List<LevelRecord> levels) {
        Set<Integer> seen = new HashSet<>();
        for (LevelRecord record : levels) {
            if (record.levelNumber() > lastUnlockedLevel) {
                throw new IllegalArgumentException(
                        "Level " + record.levelNumber() + " was won but only " + lastUnlockedLevel + " are unlocked");
            }
            if (!seen.add(record.levelNumber())) {
                throw new IllegalArgumentException("Level " + record.levelNumber() + " appears twice");
            }
        }
    }
}