package model.game.loss;

import model.tile.StorageResult;

public class EarlyKeyStorageCondition extends StorageLossCondition {

    /** Lost as soon as a key box reaches its point before opening its door (KeyBox decides it). */
    @Override
    protected boolean matches(StorageResult result) {
        return result == StorageResult.KEY_NOT_USED;
    }

    @Override
    public LossReason getReason() {
        return LossReason.EARLY_KEY_STORAGE;
    }
}
