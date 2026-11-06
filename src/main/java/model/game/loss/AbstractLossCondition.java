package model.game.loss;

import model.board.Board;
import model.board.Push;
import model.game.Level;

public abstract class AbstractLossCondition implements LossCondition {

    @Override
    public final boolean isMet(Level level) {
        Push push = level.lastPush();
        return push != null && appliesTo(push) && holds(level.getBoard(), push);
    }

    protected abstract boolean appliesTo(Push push);

    protected abstract boolean holds(Board board, Push push);
}
