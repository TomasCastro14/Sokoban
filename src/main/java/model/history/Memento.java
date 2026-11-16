package model.history;

public interface Memento {

    Memento NOTHING_TO_RESTORE = () -> { };

    void restore();
}