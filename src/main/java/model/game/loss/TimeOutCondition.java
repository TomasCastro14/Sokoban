package model.game.loss;

import model.game.Level;

public class TimeOutCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        return level.getRemainingSeconds() <= 0;
    }

    @Override
    public LossReason getReason() {
        return LossReason.TIME_OUT;
    }
}
