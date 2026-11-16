package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import model.board.Position;

import java.util.List;
import java.util.Map;

/**
 * Every group letter must be complete: a key tile needs at least one door of its letter and a door needs
 * at least one key tile. Several key tiles of one group are valid: each one opens the whole group, so
 * the extra ones are just another way to open it. The error points at the first element left without a partner.
 */
class DoorKeyGroupRule implements LevelRule {

    @Override
    public void check(LevelBuilder level) throws InvalidLevelException {
        Map<Character, List<Position>> keyTiles = level.keyTilesByGroup();
        Map<Character, List<Position>> doors = level.doorsByGroup();
        for (Map.Entry<Character, List<Position>> group : keyTiles.entrySet()) {
            if (!doors.containsKey(group.getKey())) {
                throw missing(level, group.getValue().get(0),
                        "la baldosa '" + Character.toLowerCase(group.getKey()) + "' no tiene ninguna puerta '" + group.getKey() + "'");
            }
        }
        for (Map.Entry<Character, List<Position>> group : doors.entrySet()) {
            if (!keyTiles.containsKey(group.getKey())) {
                throw missing(level, group.getValue().get(0),
                        "la puerta '" + group.getKey() + "' no tiene ninguna baldosa '" + Character.toLowerCase(group.getKey()) + "'");
            }
        }
    }

    private static InvalidLevelException missing(LevelBuilder level, Position at, String detail) {
        return new InvalidLevelException(LevelFormatRule.DOOR_KEY_MISMATCH, level.lineOf(at), detail);
    }
}
