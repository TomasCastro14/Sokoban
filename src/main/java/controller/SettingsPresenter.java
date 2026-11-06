package controller;

import service.sound.SoundService;
import view.menus.SettingsListener;

import java.util.Objects;

public final class SettingsPresenter implements SettingsListener {
    private final SoundService sound;

    public SettingsPresenter(SoundService sound) {
        this.sound = Objects.requireNonNull(sound, "sound");
    }

    @Override
    public void volumeChanged(double level) {
        sound.setVolume(level);
    }

    @Override
    public void muteToggled(boolean muted) {
        sound.setMuted(muted);
    }

    @Override
    public double currentVolume() {
        return sound.volume();
    }

    @Override
    public boolean isMuted() {
        return sound.isMuted();
    }
}
