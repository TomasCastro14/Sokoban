package model.tile;

public interface TileMemento {

    TileMemento NOTHING_TO_RESTORE = () -> { };

    void restore();
}