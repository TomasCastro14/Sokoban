package service.sound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SoundServiceTest {

    @Test
    @DisplayName("Sin mute, play reproduce el efecto")
    void playsWhenNotMuted() {
        RecordingSoundService sound = new RecordingSoundService();

        sound.play(SoundEffect.STEP);

        assertEquals(List.of(SoundEffect.STEP), sound.played());
    }

    @Test
    @DisplayName("Con mute activo no suena nada")
    void mutedPlaysNothing() {
        RecordingSoundService sound = new RecordingSoundService();
        sound.setMuted(true);

        sound.play(SoundEffect.PUSH_BOX);

        assertTrue(sound.played().isEmpty());
    }

    @Test
    @DisplayName("Al desactivar el mute vuelve el volumen elegido")
    void unmuteRestoresVolume() {
        SoundService sound = new SilentSoundService();
        sound.setVolume(0.3);

        sound.setMuted(true);
        sound.setMuted(false);

        assertFalse(sound.isMuted());
        assertEquals(0.3, sound.volume());
    }

    @Test
    @DisplayName("El volumen arranca en el valor por defecto")
    void startsWithDefaultVolume() {
        assertEquals(Volume.DEFAULT.level(), new SilentSoundService().volume());
    }

    @ParameterizedTest
    @ValueSource(doubles = {Volume.MIN, 0.5, Volume.MAX})
    @DisplayName("Se aceptan los volúmenes dentro del rango, incluidos los límites")
    void acceptsVolumeInRange(double level) {
        SoundService sound = new SilentSoundService();

        sound.setVolume(level);

        assertEquals(level, sound.volume());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 1.01, Double.NaN, Double.POSITIVE_INFINITY})
    @DisplayName("Un volumen fuera de rango se rechaza y no cambia el actual")
    void rejectsVolumeOutOfRange(double level) {
        SoundService sound = new SilentSoundService();
        sound.setVolume(0.4);

        assertThrows(IllegalArgumentException.class, () -> sound.setVolume(level));
        assertEquals(0.4, sound.volume());
    }
}
