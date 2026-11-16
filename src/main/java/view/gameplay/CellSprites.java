package view.gameplay;

import model.board.Cell;
import model.board.Visibility;
import model.box.Box;
import model.box.OccupantVisitor;
import model.box.Player;
import model.tile.Ice;
import model.tile.Inaccessible;
import model.tile.Portal;
import model.tile.StoragePoint;
import model.tile.Tile;
import model.tile.TileVisitor;
import model.tile.Wall;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import view.SpriteKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CellSprites {

    public enum Appearance {
        OUTSIDE,
        HIDDEN,
        VISIBLE,
        /** Paredes: se ven siempre, incluso en la zona oculta, y van encima de la nube. */
        ABOVE_FOG
    }

    /** Cómo se ve cada baldosa sin importar lo que vio el jugador; VISIBLE quiere decir "si ya la vio". */
    private static final TileVisitor<Appearance> FIXED_APPEARANCE = new TileVisitor<>() {
        @Override
        public Appearance visitTile(Tile tile) {
            return Appearance.VISIBLE;
        }

        @Override
        public Appearance visitInaccessible(Inaccessible inaccessible) {
            return Appearance.OUTSIDE;                  // se ve el pasto del fondo
        }

        @Override
        public Appearance visitWall(Wall wall) {
            return Appearance.ABOVE_FOG;
        }
    };

    /** Capas de la baldosa, de abajo hacia arriba: el piso y, si tiene, su decoración. */
    private static final TileVisitor<List<SpriteKey>> TILE_LAYERS = new TileVisitor<>() {
        @Override
        public List<SpriteKey> visitTile(Tile tile) {
            return List.of(SpriteKey.STONE);
        }

        @Override
        public List<SpriteKey> visitInaccessible(Inaccessible inaccessible) {
            return List.of();
        }

        @Override
        public List<SpriteKey> visitWall(Wall wall) {
            return List.of(SpriteKey.WALL);
        }

        @Override
        public List<SpriteKey> visitIce(Ice ice) {
            return List.of(SpriteKey.ICE);
        }

        @Override
        public List<SpriteKey> visitStoragePoint(StoragePoint point) {
            return List.of(SpriteKey.STONE, SpriteKey.savepoint(point.getType(), point.isFilled()));
        }

        @Override
        public List<SpriteKey> visitPortal(Portal portal) {
            return List.of(SpriteKey.STONE, SpriteKey.PORTAL);
        }

        @Override
        public List<SpriteKey> visitDoor(Door door) {
            return door.isOpen() ? List.of(SpriteKey.STONE) : List.of(SpriteKey.STONE, SpriteKey.DOOR);
        }

        @Override
        public List<SpriteKey> visitKeyTile(KeyTile key) {
            return key.isUsed() ? List.of(SpriteKey.STONE) : List.of(SpriteKey.STONE, SpriteKey.KEY_ITEM);
        }
    };

    /** La caja que hay que dibujar en el casillero; el jugador se dibuja aparte, con su animación. */
    private static final OccupantVisitor<Optional<SpriteKey>> CRATE = new OccupantVisitor<>() {
        @Override
        public Optional<SpriteKey> visitPlayer(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<SpriteKey> visitBox(Box box) {
            return Optional.of(SpriteKey.crate(box.getType()));
        }
    };

    private CellSprites() { }

    public static Appearance appearanceOf(Cell cell, Visibility visibility) {
        Appearance fixed = cell.getTile().accept(FIXED_APPEARANCE);
        if (fixed == Appearance.VISIBLE && !visibility.isVisible(cell.getPosition())) {
            return Appearance.HIDDEN;
        }
        return fixed;
    }

    public static List<SpriteKey> layers(Cell cell) {
        List<SpriteKey> layers = new ArrayList<>(cell.getTile().accept(TILE_LAYERS));
        if (!cell.isFree()) {
            cell.getOccupant().accept(CRATE).ifPresent(layers::add);
        }
        return layers;
    }
}
