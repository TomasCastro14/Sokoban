package controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.sound.SilentSoundService;
import service.sound.SoundService;

import static org.junit.jupiter.api.Assertions.*;

class SettingsPresenterTest {

    @Test
    @DisplayName("Mover el slider cambia el volumen del servicio")
    void volumeChangeUpdatesService() {
        SoundService sound = new SilentSoundService();

        new SettingsPresenter(sound).volumeChanged(0.25);

        assertEquals(0.25, sound.volume());
    }

    @Test
    @DisplayName("Tildar silenciar mutea el servicio sin perder el volumen")
    void muteToggleUpdatesService() {
        SoundService sound = new SilentSoundService();
        SettingsPresenter presenter = new SettingsPresenter(sound);
        presenter.volumeChanged(0.6);

        presenter.muteToggled(true);

        assertTrue(sound.isMuted());
        assertEquals(0.6, presenter.currentVolume());
    }

    @Test
    @DisplayName("La vista arranca con el estado actual del servicio")
    void exposesCurrentState() {
        SoundService sound = new SilentSoundService();
        sound.setVolume(0.1);
        sound.setMuted(true);

        SettingsPresenter presenter = new SettingsPresenter(sound);

        assertEquals(0.1, presenter.currentVolume());
        assertTrue(presenter.isMuted());
    }
}
