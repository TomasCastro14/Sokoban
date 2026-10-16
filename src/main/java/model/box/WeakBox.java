package model.box;

public class WeakBox extends Box {
    private int remainingPushes;

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
    public PushResult push() {
        throw new UnsupportedOperationException("TODO");
    }
}
