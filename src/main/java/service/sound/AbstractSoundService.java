package service.sound;

import java.util.Objects;

public abstract class AbstractSoundService extends AbstractAudioControl implements SoundService {

    @Override
    public final void play(SoundEffect effect) {
        Objects.requireNonNull(effect, "effect");
        if (!isMuted()) {
            emit(effect, volume());
        }
    }

    protected abstract void emit(SoundEffect effect, double level);
}