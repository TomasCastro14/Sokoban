package model.game.event;

import model.game.LevelStats;

public interface LevelCompletedListener {

    void levelCompleted(int levelNumber, LevelStats stats);
}