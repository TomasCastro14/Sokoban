package model.game;

import model.game.score.LevelGoals;

import java.time.Duration;
import java.util.Objects;

/**
 * The level data from the file header, already typed. It is the single owner of its invariants:
 * the parser reuses the same checks so each rule lives in one place.
 *
 * @param name      shown to the player, not blank
 * @param timeLimit time to solve the level, greater than zero
 * @param goals     the three goals that give the stars
 */
public record LevelHeader(String name, Duration timeLimit, LevelGoals goals) {

    public LevelHeader {
        requireName(name);
        requireTimeLimit(timeLimit);
        Objects.requireNonNull(goals, "goals");
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
}
