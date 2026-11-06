package view.gameplay.render;

import model.tile.TileKind;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class TileRendererRegistry {
    private final Map<TileKind, TileRenderer> renderers = new EnumMap<>(TileKind.class);

    public static TileRendererRegistry standard() {
        return new TileRendererRegistry()
                .register(TileKind.FLOOR, new FloorRenderer())
                .register(TileKind.WALL, new WallRenderer())
                .register(TileKind.INACCESSIBLE, new InaccessibleRenderer())
                .register(TileKind.ICE, new IceRenderer())
                .register(TileKind.PORTAL, new PortalRenderer())
                .register(TileKind.STORAGE, new StorageRenderer())
                .register(TileKind.DOOR, new DoorRenderer())
                .register(TileKind.KEY, new KeyTileRenderer());
    }

    public TileRendererRegistry register(TileKind kind, TileRenderer renderer) {
        renderers.put(Objects.requireNonNull(kind, "kind"), Objects.requireNonNull(renderer, "renderer"));
        return this;
    }

    public TileRenderer rendererFor(TileKind kind) {
        TileRenderer renderer = renderers.get(kind);
        if (renderer == null) {
            throw new IllegalStateException("No hay un TileRenderer registrado para " + kind);
        }
        return renderer;
    }
}