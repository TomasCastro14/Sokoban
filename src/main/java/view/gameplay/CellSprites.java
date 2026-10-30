package view.gameplay;

import model.board.Cell;
import model.board.Visibility;
import model.box.Box;
import model.box.Occupant;
import model.tile.Ice;
import model.tile.Inaccessible;
import model.tile.Portal;
import model.tile.StoragePoint;
import model.tile.Tile;
import model.tile.Wall;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import view.SpriteKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Traduce una celda del modelo a "qué hay que dibujar". Es pura: no toca JavaFX ni imágenes,
 * así que se testea con el modelo. Si aparece un casillero nuevo, se toca solo esta clase.
 */
public final class CellSprites {

    public enum Appearance {
        /** Fuera del warehouse (Inaccessible): no se dibuja, se ve el pasto de fondo. */
        OUTSIDE,
        /** Dentro del warehouse pero todavía no visto (tras una puerta cerrada). */
        HIDDEN,
        VISIBLE
    }

    private CellSprites() { }

    public static Appearance appearanceOf(Cell cell, Visibility visibility) {
        if (cell.getTile() instanceof Inaccessible) {
            return Appearance.OUTSIDE;
        }
        return visibility.isVisible(cell.getPosition()) ? Appearance.VISIBLE : Appearance.HIDDEN;
    }

    /**
     * Capas de abajo hacia arriba: piso, decoración del casillero y caja.
     * El jugador no está acá: se dibuja aparte porque se anima entre casilleros.
     * Un punto de guardado lleno ya trae su caja dibujada en el sprite del casillero.
     */
    public static List<SpriteKey> layers(Cell cell) {
        Tile tile = cell.getTile();
        List<SpriteKey> layers = new ArrayList<>(3);
        layers.add(ground(tile));
        decoration(tile, layers);
        Occupant occupant = cell.getOccupant();
        if (occupant instanceof Box box) {
            layers.add(SpriteKey.crate(box.getType()));
        }
        return layers;
    }

    private static SpriteKey ground(Tile tile) {
        return switch (tile) {
            case Wall w -> SpriteKey.WALL;
            case Ice i -> SpriteKey.ICE;
            default -> SpriteKey.STONE;
        };
    }

    private static void decoration(Tile tile, List<SpriteKey> layers) {
        switch (tile) {
            case StoragePoint point -> layers.add(SpriteKey.savepoint(point.getType(), point.isFilled()));
            case Door door -> {
                if (!door.isOpen()) {
                    layers.add(SpriteKey.DOOR);
                }
            }
            case KeyTile k -> layers.add(SpriteKey.KEY_ITEM);   // al usarse el tablero la cambia por piso
            case Portal p -> layers.add(SpriteKey.PORTAL);
            default -> { }
        }
    }
}
