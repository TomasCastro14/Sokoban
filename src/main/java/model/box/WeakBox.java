package model.box;

public class WeakBox extends Box {
    private int remainingPushes;

    /** @param maxPushes pushes the box withstands; the next one breaks it */
    public WeakBox(int maxPushes) {
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
    protected PushResult onPushed() {
        if (remainingPushes == 0) {
            return PushResult.BROKEN;
        }
        remainingPushes--;
        return PushResult.PUSHED;
    }
}
