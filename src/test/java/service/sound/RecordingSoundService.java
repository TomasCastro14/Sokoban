package service.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecordingSoundService extends AbstractSoundService {
    private final List<SoundEffect> played = new ArrayList<>();

    @Override
    protected void emit(SoundEffect effect, double level) {
        played.add(effect);
    }

    public List<SoundEffect> played() {
        return Collections.unmodifiableList(played);
    }
}
