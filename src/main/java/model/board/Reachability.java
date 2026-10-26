package model.board;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Pure Fabrication: the area connected to a position (BFS).
 * It only asks each tile whether it blocks vision, so walls and closed doors stop it
 * and open doors let it through without knowing what a door is.
 * Boxes and empty storage points (holes) do not stop it: the player sees across them.
 */
public class Reachability {

    public Set<Position> from(Board board, Position start) {
        Set<Position> reached = new HashSet<>();
        Deque<Position> pending = new ArrayDeque<>();
        reached.add(start);
        pending.add(start);
        while (!pending.isEmpty()) {
            Position current = pending.poll();
            for (Direction d : Direction.values()) {
                Position next = current.move(d);
                if (board.isInside(next) && !board.getTile(next).blocksVision() && reached.add(next)) {
                    pending.add(next);
                }
            }
        }
        return reached;
    }
}
