package model.game.score;

import model.game.LevelStats;

import java.util.List;

/**
 * Every met goal is one star and {@value #POINTS_PER_GOAL} points,
 * and every second left multiplies those points (2 goals with 50 s left = 10.000 x 50 = 500.000).
 */
public final class StandardScoreCalculator implements ScoreCalculator {
    static final int POINTS_PER_GOAL = 5_000;

    @Override
    public ScoreResult calculate(LevelGoals goals, LevelStats stats, int remainingSeconds) {
        if (remainingSeconds < 0) {
            throw new IllegalArgumentException("Remaining seconds cannot be negative, got " + remainingSeconds);
        }
        List<GoalResult> results = goals.evaluate(stats);
        int metGoals = (int) results.stream().filter(GoalResult::isMet).count();
        int score = Math.multiplyExact(metGoals * POINTS_PER_GOAL, remainingSeconds);
        return new ScoreResult(score, metGoals, results);
    }
}
