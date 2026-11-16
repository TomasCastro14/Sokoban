package model.board;

import java.util.Set;

/**
 * Protected Variations: who needs the reachable area depends on this, not on the algorithm.
 * It only reads the board; it never moves, opens or reveals anything.
 */
public interface ReachabilityCalculator {

    /** Every position connected to start, start included. The set cannot be modified. */
    Set<Position> from(Board board, Position start);
}
