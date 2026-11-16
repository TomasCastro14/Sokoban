package model.game.loss;

import model.game.event.TimeListener;

import java.util.Objects;

public final class TimeLossListener implements TimeListener {
    private final LossEvaluator evaluator;

    public TimeLossListener(LossEvaluator evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator");
    }

    @Override
    public void secondElapsed(int remainingSeconds) {
        evaluator.evaluate();
    }
}