package model.tile.mechanism;

import model.box.Box;
import model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer subject: the first key box that lands on the tile activates it and
 * the doors of its group are told, without the tile knowing where they are.
 * A key tile works only once.
 */
public class KeyTile extends Tile {
    private final char group;
    private final List<KeyTileListener> listeners = new ArrayList<>();
    private boolean used;

    public KeyTile(char group) {
        this.group = Character.toUpperCase(group);
    }

    /** Group letter in upper case: key tile 'a' opens doors 'A'. */
    public char getGroup() {
        return group;
    }

    public boolean isUsed() {
        return used;
    }

    /** A key tile works once: after it opens its doors the board replaces it with floor. */
    @Override
    public boolean isConsumed() {
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

    private void notifyListeners() {
        for (KeyTileListener listener : listeners) {
            listener.keyTileActivated(group);
        }
    }
}
