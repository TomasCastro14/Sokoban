package model.game.loss;

import model.tile.StorageResult;

public class WrongStorageCondition extends StorageLossCondition {

    /** Lost as soon as a box is pushed onto a point of another color. */
    @Override
    protected boolean matches(StorageResult result) {
        return result == StorageResult.WRONG_BOX;
    }

    @Override
    public LossReason getReason() {
        return LossReason.WRONG_STORAGE;
    }
}
