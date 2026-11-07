package model.game.score;

import model.game.LevelStats;

import java.util.List;

/** The three goals written in the level header. Each met goal is worth one star. */
public record LevelGoals(int maxMoves, int maxPushes, int maxUndos) {

    public LevelGoals {
        Goal.requireLimit(maxMoves);
        Goal.requireLimit(maxPushes);
        Goal.requireLimit(maxUndos);
    }

    /** In the order they are shown to the player. */
    public List<Goal> all() {
        return List.of(
                new Goal(GoalType.MOVES, maxMoves),
                new Goal(GoalType.PUSHES, maxPushes),
                new Goal(GoalType.UNDOS, maxUndos));
    }

    public List<GoalResult> evaluate(LevelStats stats) {
        return all().stream().map(goal -> goal.evaluate(stats)).toList();
    }
}
