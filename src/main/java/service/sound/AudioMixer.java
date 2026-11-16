package service.sound;

import java.util.List;
import java.util.Objects;

/** Composite: one set of controls (the settings screen) driving every audio channel at once. */
public final class AudioMixer extends AbstractAudioControl {
    private final List<AudioControl> channels;

    public AudioMixer(List<? extends AudioControl> channels) {
        this.channels = List.copyOf(Objects.requireNonNull(channels, "channels"));
        settingsChanged();
    }

    @Override
    protected void settingsChanged() {
        for (AudioControl channel : channels) {
            channel.setVolume(volume());
            channel.setMuted(isMuted());
        }
    }
}