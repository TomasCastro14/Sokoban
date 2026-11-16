package model.game.event;

import model.board.Direction;
import model.board.Visibility;
import model.game.Level;
import model.game.LevelStats;
import model.game.loss.LossReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Observer de prueba: anota el nombre de cada método que el nivel le llama, en orden. */
public final class EventRecorder implements BoardListener, LevelOutcomeListener {
    private final List<String> events = new ArrayList<>();

    public static EventRecorder attachTo(Level level) {
        EventRecorder recorder = new EventRecorder();
        level.addBoardListener(recorder);
        level.addOutcomeListener(recorder);
        return recorder;
    }

    public List<String> events() {
        return Collections.unmodifiableList(events);
    }

    public boolean received(String event) {
        return events.contains(event);
    }

    public long count(String event) {
        return events.stream().filter(event::equals).count();
    }

    public void clear() {
        events.clear();
    }

    @Override public void playerMoved(Direction direction) { events.add("playerMoved"); }
    @Override public void boxPushed(Direction direction) { events.add("boxPushed"); }
    @Override public void boxStored(int storedBoxes, int totalBoxes) { events.add("boxStored"); }
    @Override public void doorOpened(Visibility visibility) { events.add("doorOpened"); }
    @Override public void levelWon(LevelStats stats) { events.add("levelWon"); }
    @Override public void levelLost(LossReason reason) { events.add("levelLost"); }
}