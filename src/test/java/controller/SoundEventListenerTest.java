package controller;

import model.board.Direction;
import model.game.LevelStats;
import model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.sound.RecordingSoundService;
import service.sound.SoundEffect;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SoundEventListenerTest {

    static Stream<Arguments> eventsAndSounds() {
        return Stream.of(
                event("playerMoved", l -> l.playerMoved(Direction.RIGHT), SoundEffect.STEP),
                event("boxPushed", l -> l.boxPushed(Direction.LEFT), SoundEffect.PUSH_BOX),
                event("boxStored", l -> l.boxStored(1, 2), SoundEffect.BOX_ON_TARGET),
                event("doorOpened", l -> l.doorOpened(null), SoundEffect.DOOR_OPEN),
                event("boardRestored", l -> l.boardRestored(null), SoundEffect.UNDO),
                event("levelWon", l -> l.levelWon(new LevelStats()), SoundEffect.LEVEL_WON),
                event("levelLost", l -> l.levelLost(LossReason.DEADLOCK), SoundEffect.LEVEL_LOST));
    }

    private static Arguments event(String name, Consumer<SoundEventListener> fire, SoundEffect expected) {
        return Arguments.of(name, fire, expected);
    }

    @ParameterizedTest(name = "{0} -> {2}")
    @MethodSource("eventsAndSounds")
    @DisplayName("Cada evento del modelo dispara su SoundEffect")
    void eachEventPlaysItsSound(String name, Consumer<SoundEventListener> fire, SoundEffect expected) {
        RecordingSoundService sound = new RecordingSoundService();

        fire.accept(new SoundEventListener(sound));

        assertEquals(List.of(expected), sound.played());
    }

    @Test
    @DisplayName("Iniciar o reiniciar un nivel no suena")
    void levelFlowIsSilent() {
        RecordingSoundService sound = new RecordingSoundService();
        SoundEventListener listener = new SoundEventListener(sound);

        listener.levelStarted(1, null);
        listener.levelRestarted(1, null);

        assertTrue(sound.played().isEmpty());
    }

    @Test
    @DisplayName("Con mute activo los eventos no suenan")
    void mutedEventsAreSilent() {
        RecordingSoundService sound = new RecordingSoundService();
        sound.setMuted(true);

        new SoundEventListener(sound).boxPushed(Direction.UP);

        assertTrue(sound.played().isEmpty());
    }
}
