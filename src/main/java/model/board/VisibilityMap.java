package model.board;

import model.history.Memento;

import java.util.HashSet;
import java.util.Set;

public class VisibilityMap implements Visibility {
    private final ReachabilityCalculator reachability;
    private final Set<Position> visible = new HashSet<>();

    public VisibilityMap(ReachabilityCalculator reachability) {
        this.reachability = reachability;
    }

    @Override
    public boolean isVisible(Position position) {
        return visible.contains(position);
    }

    public void recalculate(Board board, Position from) {
        for (Position reached : reachability.from(board, from)) {
            revealWithSurroundings(board, reached);
        }
    }

    public Memento save() {
        Set<Position> saved = Set.copyOf(visible);
        return () -> {
            visible.clear();
            visible.addAll(saved);
        };
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