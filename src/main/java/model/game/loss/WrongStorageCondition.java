package model.game.loss;

import model.game.Level;

public class WrongStorageCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public LossReason getReason() {
        return LossReason.WRONG_STORAGE;
    }
}
