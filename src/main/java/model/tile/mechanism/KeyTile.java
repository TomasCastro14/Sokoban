package model.tile.mechanism;

import model.box.Box;
import model.history.Memento;
import model.tile.Tile;
import model.tile.TileKind;
import model.tile.TileLook;

import java.util.ArrayList;
import java.util.List;

public class KeyTile extends Tile {
    private final char group;
    private final List<KeyTileListener> listeners = new ArrayList<>();
    private boolean used;

    public KeyTile(char group) {
        this.group = Character.toUpperCase(group);
    }

    public char getGroup() {
        return group;
    }

    public boolean isUsed() {
        return used;
    }

    public void addListener(KeyTileListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onBoxEntered(Box box) {
        if (!used && box.activateKeyTile()) {
            used = true;
            notifyListeners();
        }
    }

    @Override
    public TileLook look() {
        return TileLook.of(TileKind.KEY, used);
    }

    @Override
    public Memento save() {
        return new KeyTileMemento(this, used);
    }

    private void notifyListeners() {
        for (KeyTileListener listener : listeners) {
            listener.keyTileActivated(group);
        }
    }

    private static final class KeyTileMemento implements Memento {
        private final KeyTile tile;
        private final boolean used;

        private KeyTileMemento(KeyTile tile, boolean used) {
            this.tile = tile;
            this.used = used;
        }

        @Override
        public void restore() {
            tile.used = used;
        }
    }
}