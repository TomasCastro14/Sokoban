package model.game.loss;

import model.board.Direction;
import model.board.Visibility;
import model.game.event.BoardListener;

import java.util.Objects;

public final class MoveLossListener implements BoardListener {
    private final LossEvaluator evaluator;

    public MoveLossListener(LossEvaluator evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator");
    }

    @Override
    public void boxPushed(Direction direction) {
        evaluator.evaluate();
    }

    @Override
    public void playerMoved(Direction direction) {
    }

    @Override
    public void boxStored(int storedBoxes, int totalBoxes) {
    }

    @Override
    public void doorOpened(Visibility visibility) {
    }
}