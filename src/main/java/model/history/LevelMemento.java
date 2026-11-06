package model.history;

import java.util.List;

public final class LevelMemento implements Memento {

    static final LevelMemento EMPTY = new LevelMemento(List.of());

    private final List<Memento> parts;

    public LevelMemento(List<Memento> parts) {
        this.parts = List.copyOf(parts);
    }

    @Override
    public void restore() {
        parts.forEach(Memento::restore);
    }
}