package model.game.score;

import model.game.LevelStats;

/** Strategy: the scoring rule is decided by the team and can change without touching the level. */
public interface ScoreCalculator {

    ScoreResult calculate(LevelGoals goals, LevelStats stats, int remainingSeconds);
}
