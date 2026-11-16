package service.sound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SoundEffectAssetsTest {

    @ParameterizedTest
    @EnumSource(SoundEffect.class)
    @DisplayName("Cada SoundEffect tiene su archivo en los recursos")
    void everyEffectHasItsAsset(SoundEffect effect) {
        assertNotNull(SoundEffect.class.getResource(effect.resourcePath()),
                () -> "Falta el audio " + effect.resourcePath());
    }
}
