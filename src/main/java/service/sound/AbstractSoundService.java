package service.sound;

import java.util.Objects;

public abstract class AbstractSoundService implements SoundService {
    private Volume volume = Volume.DEFAULT;
    private boolean muted;

    @Override
    public final void play(SoundEffect effect) {
        Objects.requireNonNull(effect, "effect");
        if (!muted) {
            emit(effect, volume.level());
        }
    }

    protected abstract void emit(SoundEffect effect, double level);

    @Override
    public final void setVolume(double level) {
        volume = new Volume(level);
    }

    @Override
    public final double volume() {
        return volume.level();
    }

    @Override
    public final void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public final boolean isMuted() {
        return muted;
    }
}
