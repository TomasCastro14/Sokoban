package factory;

import exception.InvalidLevelException;
import model.board.Position;

/**
 * Pure Fabrication: the border of the map is the rectangle that encloses every row.
 * Each border cell must hold a boundary tile; cells a short row lacks are padded
 * with void, so they are boundary too. Reports the first open cell in reading order.
 */
class BorderRule implements LevelRule {
    static final String RULE = "The map border must be wall or void";

    @Override
    public void check(LevelBuilder level) throws InvalidLevelException {
        for (int row = 0; row < level.rows(); row++) {
            for (int col = 0; col < level.cols(); col++) {
                Position p = new Position(row, col);
                if (isBorder(level, p) && !isBoundary(level.contentAt(p))) {
                    throw new InvalidLevelException(RULE, level.lineOf(p));
                }
            }
        }
    }

    private static boolean isBorder(LevelBuilder level, Position p) {
        return p.row() == 0 || p.col() == 0 || p.row() == level.rows() - 1 || p.col() == level.cols() - 1;
    }

    /** A missing cell is padded with void, which closes the map. */
    private static boolean isBoundary(CellContent content) {
        return content == null || content.tile().isBoundary();
    }
}
