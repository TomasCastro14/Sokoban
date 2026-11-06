package model.box;

import model.history.Memento;

public class WeakBox extends Box {
    public static final int MIN_PUSHES = 1;
    public static final int MAX_PUSHES = 9;

    private int remainingPushes;

    public WeakBox(int maxPushes) {
        if (maxPushes < MIN_PUSHES || maxPushes > MAX_PUSHES) {
            throw new IllegalArgumentException(
                    "A weak box withstands " + MIN_PUSHES + ".." + MAX_PUSHES + " pushes, got " + maxPushes);
        }
        this.remainingPushes = maxPushes;
    }

    public int getRemainingPushes() {
        return remainingPushes;
    }

    @Override
    public BoxType getType() {
        return BoxType.WEAK;
    }

    @Override
    public Memento save() {
        int savedPushes = remainingPushes;
        return () -> remainingPushes = savedPushes;
    }

    @Override
    protected PushResult onPushed() {
        if (remainingPushes == 0) {
            return PushResult.BROKEN;
        }
        remainingPushes--;
        return PushResult.PUSHED;
    }
}