package model.tile;

import model.box.BoxType;

import java.util.Objects;

public record TileLook(TileKind kind, BoxType color, boolean active) {

    public TileLook {
        Objects.requireNonNull(kind, "kind");
    }

    public static TileLook of(TileKind kind) {
        return new TileLook(kind, null, false);
    }

    public static TileLook of(TileKind kind, boolean active) {
        return new TileLook(kind, null, active);
    }
}