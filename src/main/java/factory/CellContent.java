package factory;

import model.box.Occupant;
import model.tile.Tile;

/** What a map character produces: a tile and, optionally, an occupant (player or box). */
public record CellContent(Tile tile, Occupant occupant) {
}
