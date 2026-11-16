package model.game.score;

import model.game.LevelStats;

import java.util.Objects;

/**
 * One level goal: finish the level using at most {@code limit} of {@code type}.
 * It is the single owner of the limit rule, so the header parser reuses {@link #requireLimit}.
 */
public record Goal(GoalType type, int limit) {
    private static final int MIN_LIMIT = 0;

    public Goal {
        Objects.requireNonNull(type, "type");
        requireLimit(limit);
    }

    public static int requireLimit(int limit) {
        if (limit < MIN_LIMIT) {
            throw new IllegalArgumentException("A goal limit cannot be negative, got " + limit);
        }
        return limit;
    }

    public GoalResult evaluate(LevelStats stats) {
        return new GoalResult(this, type.countIn(stats));
    }
}
