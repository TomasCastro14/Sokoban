package model.game.score;

import java.util.Objects;

/** How the player did against one goal: what was allowed and what was actually used. */
public record GoalResult(Goal goal, int achieved) {

    public GoalResult {
        Objects.requireNonNull(goal, "goal");
    }

    public boolean isMet() {
        return achieved <= goal.limit();
    }
}
