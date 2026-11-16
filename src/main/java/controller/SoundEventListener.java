package controller;

import model.board.Direction;
import model.board.Visibility;
import model.game.Level;
import model.game.LevelStats;
import model.game.event.BoardListener;
import model.game.event.LevelFlowListener;
import model.game.event.LevelOutcomeListener;
import model.game.loss.LossReason;
import service.sound.SoundEffect;
import service.sound.SoundService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class SoundEventListener implements BoardListener, LevelOutcomeListener, LevelFlowListener {

    private enum SoundTrigger {
        PLAYER_MOVED, BOX_PUSHED, BOX_STORED, DOOR_OPENED, MOVE_UNDONE, LEVEL_WON, LEVEL_LOST
    }

    private static final Map<SoundTrigger, SoundEffect> SOUNDS = soundTable();

    private final SoundService sound;

    public SoundEventListener(SoundService sound) {
        this.sound = Objects.requireNonNull(sound, "sound");
    }

    private static Map<SoundTrigger, SoundEffect> soundTable() {
        Map<SoundTrigger, SoundEffect> table = new EnumMap<>(SoundTrigger.class);
        table.put(SoundTrigger.PLAYER_MOVED, SoundEffect.STEP);
        table.put(SoundTrigger.BOX_PUSHED, SoundEffect.PUSH_BOX);
        table.put(SoundTrigger.BOX_STORED, SoundEffect.BOX_ON_TARGET);
        table.put(SoundTrigger.DOOR_OPENED, SoundEffect.DOOR_OPEN);
        table.put(SoundTrigger.MOVE_UNDONE, SoundEffect.UNDO);
        table.put(SoundTrigger.LEVEL_WON, SoundEffect.LEVEL_WON);
        table.put(SoundTrigger.LEVEL_LOST, SoundEffect.LEVEL_LOST);
        return Collections.unmodifiableMap(table);
    }

    private void emit(SoundTrigger trigger) {
        sound.play(SOUNDS.get(trigger));
    }

    @Override public void playerMoved(Direction direction) { emit(SoundTrigger.PLAYER_MOVED); }
    @Override public void boxPushed(Direction direction) { emit(SoundTrigger.BOX_PUSHED); }
    @Override public void boxStored(int storedBoxes, int totalBoxes) { emit(SoundTrigger.BOX_STORED); }
    @Override public void doorOpened(Visibility visibility) { emit(SoundTrigger.DOOR_OPENED); }
    @Override public void boardRestored(Level level) { emit(SoundTrigger.MOVE_UNDONE); }
    @Override public void levelWon(LevelStats stats) { emit(SoundTrigger.LEVEL_WON); }
    @Override public void levelLost(LossReason reason) { emit(SoundTrigger.LEVEL_LOST); }

    @Override public void levelStarted(int levelNumber, Level level) { }
    @Override public void levelRestarted(int levelNumber, Level level) { }
}
