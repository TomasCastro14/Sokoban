package view.gameplay.render;

import view.SpriteKey;

import java.util.List;

final class Layers {
    static final List<SpriteKey> FLOOR = List.of(SpriteKey.STONE);

    private Layers() {
    }

    static List<SpriteKey> onFloor(SpriteKey decoration) {
        return List.of(SpriteKey.STONE, decoration);
    }
}