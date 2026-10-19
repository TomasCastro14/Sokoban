package model.game.score;

import model.game.LevelStats;

public interface ScoreCalculator {

    ScoreResult calculate(LevelStats stats, int remainingSeconds, int storedBoxes, int openedDoors, int targetScore);
}
