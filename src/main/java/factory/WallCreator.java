package factory;

import model.board.Position;
import model.tile.Wall;

public class WallCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Wall(), null);
    }
}
