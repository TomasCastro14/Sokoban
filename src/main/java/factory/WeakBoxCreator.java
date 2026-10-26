package factory;

import model.board.Position;
import model.box.WeakBox;
import model.tile.Floor;

/**
 * Concrete creator for weak boxes: the digit is the number of pushes the box withstands.
 * Its symbols come from WeakBox's own range, so the range has a single owner.
 */
public class WeakBoxCreator implements CellCreator {
    private static final int RADIX = 10;

    /** The digits this creator handles, from WeakBox.MIN_PUSHES to WeakBox.MAX_PUSHES. */
    public static String symbols() {
        StringBuilder symbols = new StringBuilder();
        for (int pushes = WeakBox.MIN_PUSHES; pushes <= WeakBox.MAX_PUSHES; pushes++) {
            symbols.append(Character.forDigit(pushes, RADIX));
        }
        return symbols.toString();
    }

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Floor(), new WeakBox(Character.digit(symbol, RADIX)));
    }
}
