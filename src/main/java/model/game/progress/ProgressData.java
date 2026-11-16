package model.game.progress;

import java.util.Map;
import java.util.Objects;

/**
 * Memento of the progress: a plain, immutable copy of what {@link Progress} remembers,
 * so it can be saved and restored without exposing the Progress internals.
 *
 * @param lastUnlockedLevel the highest unlocked level
 * @param starsByLevel      best stars of every won level
 * @param bestScoreByLevel  best score of every won level
 */
public record ProgressData(int lastUnlockedLevel,
                           Map<Integer, Integer> starsByLevel,
                           Map<Integer, Integer> bestScoreByLevel) {

    public ProgressData {
        starsByLevel = Map.copyOf(Objects.requireNonNull(starsByLevel, "starsByLevel"));
        bestScoreByLevel = Map.copyOf(Objects.requireNonNull(bestScoreByLevel, "bestScoreByLevel"));
    }
}
