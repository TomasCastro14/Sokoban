package model.game;

import java.time.Duration;
import java.util.Objects;

/**
 * The level data from the file header, already typed. It is the single owner of its invariants:
 * the parser reuses the same checks so each rule lives in one place.
 *
 * @param name        shown to the player, not blank
 * @param timeLimit   time to solve the level, greater than zero
 * @param targetScore reference score for the score strategy, zero or more
 */
public record LevelHeader(String name, Duration timeLimit, int targetScore) {
    private static final int MIN_TARGET_SCORE = 0;

    public LevelHeader {
        requireName(name);
        requireTimeLimit(timeLimit);
        requireTargetScore(targetScore);
    }

    public static String requireName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("The level name cannot be empty");
        }
        return name;
    }

    public static Duration requireTimeLimit(Duration timeLimit) {
        Objects.requireNonNull(timeLimit, "timeLimit");
        if (timeLimit.isNegative() || timeLimit.isZero()) {
            throw new IllegalArgumentException("The time limit must be greater than zero, got " + timeLimit.toSeconds() + "s");
        }
        return timeLimit;
    }

    public static int requireTargetScore(int targetScore) {
        if (targetScore < MIN_TARGET_SCORE) {
            throw new IllegalArgumentException("The target score cannot be negative, got " + targetScore);
        }
        return targetScore;
    }
}
