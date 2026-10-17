package factory;

import model.board.Position;
import model.tile.mechanism.KeyTile;

/** 'a'..'e': key tile of that group. */
public class KeyTileCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new KeyTile(symbol), null);
    }
}
