package view.gameplay;

import javafx.scene.canvas.GraphicsContext;
import model.board.Board;
import model.board.Cell;
import model.board.Direction;
import model.board.Position;
import model.board.Visibility;
import model.box.WeakBox;
import view.SpriteCatalog;
import view.SpriteKey;

/** Dibuja el globo de desgaste sobre las cajas débiles que están pegadas al jugador. */
final class WearBubbles {

    private static final double WIDTH_RATIO = 0.5;        // ancho del globo respecto del casillero
    private static final double ASPECT = 38.0 / 32.0;     // proporción del sprite (alto / ancho)
    private static final double OVERLAP_RATIO = 0.15;     // cuánto pisa el globo el borde de la caja

    private final SpriteCatalog sprites;
    private final double tile;
    private final double width;
    private final double height;

    WearBubbles(SpriteCatalog sprites, double tile) {
        this.sprites = sprites;
        this.tile = tile;
        this.width = tile * WIDTH_RATIO;
        this.height = width * ASPECT;
    }

    void draw(GraphicsContext g, Board board, Visibility visibility, double offX, double offY) {
        Position player = board.getPlayerPosition();
        for (Direction direction : Direction.values()) {
            Position near = player.move(direction);
            if (!board.isInside(near)) {
                continue;
            }
            Cell cell = board.cellAt(near);
            if (cell.getOccupant() instanceof WeakBox box
                    && CellSprites.appearanceOf(cell, visibility) == CellSprites.Appearance.VISIBLE) {
                double x = near.col() * tile - offX + (tile - width) / 2;
                double y = near.row() * tile - offY - height + tile * OVERLAP_RATIO;
                g.drawImage(sprites.get(SpriteKey.wearBubble(box.getWearState())), x, y, width, height);
            }
        }
    }
}