package model.board;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure Fabrication: breadth-first search over the board.
 * It only asks each tile whether it blocks vision, so walls and closed doors stop it
 * and open doors let it through without knowing what a door is.
 * Boxes and empty storage points (holes) do not stop it: the player sees across them.
 * Tiles may link to distant cells (portals), so it also follows each tile's linked positions.
 */
public class BfsReachability implements ReachabilityCalculator {

    @Override
    public Set<Position> from(Board board, Position start) {
        Set<Position> reached = new HashSet<>();
        Deque<Position> pending = new ArrayDeque<>();
        reached.add(start);
        pending.add(start);
        while (!pending.isEmpty()) {
            Position current = pending.poll();
            for (Position next : neighborsOf(board, current)) {
                if (isPassable(board, next) && reached.add(next)) {
                    pending.add(next);
                }
            }
        }
        return Set.copyOf(reached);
    }

    /** The four adjacent cells plus whatever the tile links to (a portal adds its pair). */
    private static List<Position> neighborsOf(Board board, Position p) {
        List<Position> neighbors = new ArrayList<>(board.getTile(p).linkedPositions());
        for (Direction d : Direction.values()) {
            neighbors.add(p.move(d));
        }
        return neighbors;
    }

    private static boolean isPassable(Board board, Position p) {
        return board.isInside(p) && !board.getTile(p).blocksVision();
    }
}
