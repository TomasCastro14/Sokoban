package model.game.score;

import java.util.List;
import java.util.Objects;

/** The end-of-level summary: points, stars and how each goal went. */
public record ScoreResult(int score, int stars, List<GoalResult> goals) {

    public ScoreResult {
        goals = List.copyOf(Objects.requireNonNull(goals, "goals"));
    }
}
