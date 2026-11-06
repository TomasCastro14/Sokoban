package view.gameplay.render;

import model.board.Cell;
import model.box.Box;
import model.box.Occupant;
import model.tile.Tile;
import view.SpriteKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CellPainter {
    private final TileRendererRegistry registry;

    public CellPainter(TileRendererRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public boolean isInsideWarehouse(Cell cell) {
        return rendererOf(cell.getTile()).isInsideWarehouse();
    }

    public List<SpriteKey> layers(Cell cell) {
        Tile tile = cell.getTile();
        List<SpriteKey> layers = new ArrayList<>(rendererOf(tile).layers(tile.look()));
        boxOn(cell).ifPresent(box -> layers.add(SpriteKey.crate(box.getType())));
        return layers;
    }

    private TileRenderer rendererOf(Tile tile) {
        return registry.rendererFor(tile.look().kind());
    }

    private static Optional<Box> boxOn(Cell cell) {
        Occupant occupant = cell.getOccupant();
        return occupant == null
                ? Optional.empty()
                : Optional.ofNullable(occupant.boxIn(cell.getTile()));
    }
}