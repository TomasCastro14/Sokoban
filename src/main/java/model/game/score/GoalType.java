package model.game.score;

import model.game.LevelStats;

import java.util.function.ToIntFunction;

/** What a level goal puts a limit on. Each type knows how to read its own count from the stats. */
public enum GoalType {
    MOVES(LevelStats::getMoves),
    PUSHES(LevelStats::getPushes),
    UNDOS(LevelStats::getUndos);

    private final ToIntFunction<LevelStats> counter;

    GoalType(ToIntFunction<LevelStats> counter) {
        this.counter = counter;
    }

    int countIn(LevelStats stats) {
        return counter.applyAsInt(stats);
    }
}
