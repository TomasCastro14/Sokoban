package port;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public record Progress(int unlockedLevel, List<LevelRecord> levels) {

    static final int FIRST_LEVEL = 1;

    public Progress {
        if (unlockedLevel < FIRST_LEVEL) {
            throw new IllegalArgumentException("The unlocked level must be at least " + FIRST_LEVEL + ", got " + unlockedLevel);
        }
        levels = List.copyOf(Objects.requireNonNull(levels, "levels"));
        requireUnlockedAndUnique(unlockedLevel, levels);
    }


    public static Progress initial() {
        return new Progress(FIRST_LEVEL, List.of());
    }

    
    public Progress withLevelWon(LevelRecord won, int levelCount) {
        int nextLevel = Math.min(won.levelNumber() + 1, levelCount);
        return new Progress(Math.max(unlockedLevel, nextLevel), withBestOf(won));
    }

    private List<LevelRecord> withBestOf(LevelRecord won) {
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

    private static void requireUnlockedAndUnique(int unlockedLevel, List<LevelRecord> levels) {
        Set<Integer> seen = new HashSet<>();
        for (LevelRecord record : levels) {
            if (record.levelNumber() > unlockedLevel) {
                throw new IllegalArgumentException(
                        "Level " + record.levelNumber() + " was won but only " + unlockedLevel + " are unlocked");
            }
            if (!seen.add(record.levelNumber())) {
                throw new IllegalArgumentException("Level " + record.levelNumber() + " appears twice");
            }
        }
    }
}