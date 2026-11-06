package model.game.loss;

import model.game.Level;

import java.util.List;
import java.util.Objects;

public final class LossEvaluator {
    private final Level level;
    private final List<LossCondition> conditions;
    private final LossReporter reporter;

    public LossEvaluator(Level level, List<LossCondition> conditions, LossReporter reporter) {
        this.level = Objects.requireNonNull(level, "level");
        this.conditions = List.copyOf(conditions);
        this.reporter = Objects.requireNonNull(reporter, "reporter");
    }

    public void evaluate() {
        conditions.stream()
                .filter(condition -> condition.isMet(level))
                .findFirst()
                .ifPresent(condition -> reporter.declareLoss(condition.getReason()));
    }
}