package model.tile;

import model.board.Position;

import java.util.Set;

/** Information Expert: knows where its pair is. Pairs are linked once, when the level is built. */
public class Portal extends Tile {
    private final char letter;
    private Position pairPosition;

    public Portal(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public void linkTo(Position pairPosition) {
        this.pairPosition = pairPosition;
    }

    /** The pair's position, or nothing while the portal is unpaired. */
    @Override
    public Set<Position> linkedPositions() {
        return pairPosition == null ? Set.of() : Set.of(pairPosition);
    }
}
