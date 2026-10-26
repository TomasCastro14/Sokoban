package model.tile.mechanism;

import java.util.ArrayList;
import java.util.List;

/** Concrete observer: opens every door of its group when a key tile of that group is activated. */
public class DoorGroup implements KeyTileListener {
    private final char group;
    private final List<Door> doors = new ArrayList<>();

    public DoorGroup(char group) {
        this.group = Character.toUpperCase(group);
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    @Override
    public void keyTileActivated(char activatedGroup) {
        if (activatedGroup != group) {
            return;
        }
        for (Door door : doors) {
            door.open();
        }
    }
}
