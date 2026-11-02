package view;

import model.box.BoxType;
import view.components.images.ImageAsset;

public enum SpriteKey implements ImageAsset {
    WINDOW_ICON("playerFace_dark.png"),

    STONE("map/background_brown.png"),
    WALL("map/block_02.png"),

    CRATE_NORMAL("crates/crate_06.png"),
    CRATE_WEAK("crates/crate_22.png"),
    CRATE_KEY("crates/crate_05.png"),

    SAVEPOINT_NORMAL("savepoint/environment_13.png"),
    SAVEPOINT_WEAK("savepoint/environment_01.png"),
    SAVEPOINT_KEY("savepoint/environment_09.png"),
    SAVEPOINT_FULL_NORMAL("savepoint/crate_41.png"),
    SAVEPOINT_FULL_WEAK("savepoint/crate_37.png"),
    SAVEPOINT_FULL_KEY("savepoint/crate_40.png"),

    DOOR("mechanics/door.png"),
    KEY_ITEM("mechanics/key.png"),
    ICE("mechanics/ice_path.png"),
    PORTAL("mechanics/portal.png"),

    PLAYER_UP("player/player_24.png"),
    PLAYER_UP_A("player/player_01.png"),
    PLAYER_UP_B("player/player_02.png"),
    PLAYER_RIGHT("player/player_09.png"),
    PLAYER_RIGHT_A("player/player_10.png"),
    PLAYER_RIGHT_B("player/player_11.png"),
    PLAYER_LEFT("player/player_12.png"),
    PLAYER_LEFT_A("player/player_13.png"),
    PLAYER_LEFT_B("player/player_14.png"),
    PLAYER_DOWN("player/player_21.png"),
    PLAYER_DOWN_A("player/player_22.png"),
    PLAYER_DOWN_B("player/player_23.png");

    private static final String ROOT = "/assets/images/";
    private final String path;

    SpriteKey(String file) {
        this.path = ROOT + file;
    }

    @Override
    public String path() {
        return path;
    }

    public static SpriteKey crate(BoxType type) {
        return switch (type) {
            case NORMAL -> CRATE_NORMAL;
            case WEAK -> CRATE_WEAK;
            case KEY -> CRATE_KEY;
        };
    }

    public static SpriteKey savepoint(BoxType type, boolean filled) {
        return switch (type) {
            case NORMAL -> filled ? SAVEPOINT_FULL_NORMAL : SAVEPOINT_NORMAL;
            case WEAK -> filled ? SAVEPOINT_FULL_WEAK : SAVEPOINT_WEAK;
            case KEY -> filled ? SAVEPOINT_FULL_KEY : SAVEPOINT_KEY;
        };
    }
}