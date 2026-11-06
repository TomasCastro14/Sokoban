package model.game.loss;

public enum LossReason {
    DEADLOCK(true),
    TIME_OUT(false),
    BROKEN_BOX(false),
    WRONG_STORAGE(false),
    EARLY_KEY_STORAGE(false);

    private final boolean recoverable;

    LossReason(boolean recoverable) {
        this.recoverable = recoverable;
    }
    
    public boolean isRecoverable() {
        return recoverable;
    }
}