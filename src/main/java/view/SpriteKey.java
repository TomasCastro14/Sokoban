package view;

import model.box.BoxType;

/**
 * Registro único de los sprites del juego: qué imagen es cada cosa y dónde vive.
 * Ninguna otra clase conoce rutas de archivos de gameplay.
 * El pasto no está acá: lo pone el CSS de la pantalla (gameplay.css).
 */
public enum SpriteKey {
    // icono del juego
    WINDOW_ICON("playerFace_dark.png"),

    // mapa
    STONE("map/background_brown.png"),
    WALL("map/block_02.png"),

    // cajas
    CRATE_NORMAL("crates/crate_06.png"),
    CRATE_WEAK("crates/crate_22.png"),
    CRATE_KEY("crates/crate_05.png"),

    // puntos de guardado vacíos y llenos
    SAVEPOINT_NORMAL("savepoint/environment_13.png"),
    SAVEPOINT_WEAK("savepoint/environment_01.png"),
    SAVEPOINT_KEY("savepoint/environment_09.png"),
    SAVEPOINT_FULL_NORMAL("savepoint/crate_41.png"),
    SAVEPOINT_FULL_WEAK("savepoint/crate_37.png"),
    SAVEPOINT_FULL_KEY("savepoint/crate_40.png"),

    // mecánicas
    DOOR("mechanics/door.png"),
    KEY_ITEM("mechanics/key.png"),
    ICE("mechanics/ice_path.png"),
    PORTAL("mechanics/portal.png"),

    // jugador: quieto, y las dos poses intermedias de caminar (A y B) por dirección
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
