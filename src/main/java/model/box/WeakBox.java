package model.box;

public class WeakBox extends Box {
    /** Range of the map digit that creates a weak box ('1'..'9'). */
    public static final int MIN_PUSHES = 1;
    public static final int MAX_PUSHES = 9;

    private int remainingPushes;

    /** @param maxPushes pushes the box withstands; the next one breaks it */
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
    protected PushResult onPushed() {
        if (remainingPushes == 0) {
            return PushResult.BROKEN;
        }
        remainingPushes--;
        return PushResult.PUSHED;
    }
}
