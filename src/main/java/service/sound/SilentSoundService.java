package service.sound;

public final class SilentSoundService extends AbstractSoundService {

    @Override
    protected void emit(SoundEffect effect, double level) {
    }
}
