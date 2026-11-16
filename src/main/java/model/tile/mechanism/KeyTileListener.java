package model.tile.mechanism;

/** Observer of key tiles: told when a key tile of a group is activated. */
public interface KeyTileListener {

    void keyTileActivated(char group);
}
