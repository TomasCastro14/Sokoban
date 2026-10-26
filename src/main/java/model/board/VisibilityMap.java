package model.board;

import java.util.HashSet;
import java.util.Set;

/**
 * Information Expert: knows which cells the player has seen.
 * Visible = every reachable cell plus the cells around it (walls, closed doors),
 * so the outline of each room is drawn. Cells are only ever added, never hidden again.
 */
public class VisibilityMap implements Visibility {
    private final Reachability reachability;
    private final Set<Position> visible = new HashSet<>();

    public VisibilityMap(Reachability reachability) {
        this.reachability = reachability;
    }

    @Override
    public boolean isVisible(Position position) {
        return visible.contains(position);
    }

    /** Reveals what can be reached from the given position, keeping what was already visible. */
    public void recalculate(Board board, Position from) {
        for (Position reached : reachability.from(board, from)) {
            revealWithSurroundings(board, reached);
        }
    }

    private void revealWithSurroundings(Board board, Position center) {
        for (int row = center.row() - 1; row <= center.row() + 1; row++) {
            for (int col = center.col() - 1; col <= center.col() + 1; col++) {
                Position p = new Position(row, col);
                if (board.isInside(p)) {
                    visible.add(p);
                }
            }
        }
    }
}
