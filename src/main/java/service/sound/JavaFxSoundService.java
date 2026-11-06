package service.sound;

import javafx.scene.media.AudioClip;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class JavaFxSoundService extends AbstractSoundService {
    private static final Logger LOG = System.getLogger(JavaFxSoundService.class.getName());

    private final Map<SoundEffect, AudioClip> clips = new EnumMap<>(SoundEffect.class);

    public JavaFxSoundService() {
        for (SoundEffect effect : SoundEffect.values()) {
            loadClip(effect).ifPresent(clip -> clips.put(effect, clip));
        }
    }

    @Override
    protected void emit(SoundEffect effect, double level) {
        AudioClip clip = clips.get(effect);
        if (clip == null) {
            return;
        }
        try {
            clip.play(level);
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Could not play sound " + effect, e);
        }
    }

    private static Optional<AudioClip> loadClip(SoundEffect effect) {
        URL resource = JavaFxSoundService.class.getResource(effect.resourcePath());
        if (resource == null) {
            LOG.log(Level.WARNING, "Missing audio file for {0}: {1}", effect, effect.resourcePath());
            return Optional.empty();
        }
        try {
            return Optional.of(new AudioClip(resource.toExternalForm()));
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Could not load sound " + effect, e);
            return Optional.empty();
        }
    }
}
