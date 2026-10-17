package factory;

import model.board.Position;
import model.box.WeakBox;
import model.tile.Floor;

/** '1'..'9': the digit is the number of pushes the box withstands. */
public class WeakBoxCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Floor(), new WeakBox(symbol - '0'));
    }
}
