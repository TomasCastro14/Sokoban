package model.game.loss;

import model.board.Board;
import model.board.MoveResult;
import model.board.Push;

public class BrokenBoxCondition extends AbstractLossCondition {

    @Override
    protected boolean appliesTo(Push push) {
        return push.result() == MoveResult.BROKEN;
    }

    @Override
    protected boolean holds(Board board, Push push) {
        return true;
    }

    @Override
    public LossReason getReason() {
        return LossReason.BROKEN_BOX;
    }
}
