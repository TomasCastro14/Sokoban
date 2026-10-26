package model.tile;

import model.board.Position;
import model.tile.effect.EntryEffect;
import model.tile.effect.NoEffect;
import model.tile.effect.TeleportEffect;

import java.util.Set;

/** Information Expert: knows where its pair is. Pairs are linked once, when the level is built. */
public class Portal extends Tile {
    private final char letter;
    private Position pairPosition;
    private EntryEffect entryEffect = new NoEffect();

    public Portal(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    /**
     * The only way to pair portals: links both directions in one step, so a pair is always
     * symmetric. Done once, when the level is built; a linked portal can never be relinked.
     */
    public static void link(Portal a, Position atA, Portal b, Position atB) {
        a.requireUnlinked();
        b.requireUnlinked();
        a.pointTo(atB);
        b.pointTo(atA);
    }

    private void requireUnlinked() {
        if (pairPosition != null) {
            throw new IllegalStateException("Portal " + letter + " is already linked to " + pairPosition);
        }
    }

    private void pointTo(Position pairPosition) {
        this.pairPosition = pairPosition;
        this.entryEffect = new TeleportEffect(pairPosition);
    }

    /** An unpaired portal behaves like floor. */
    @Override
    protected EntryEffect entryEffect() {
        return entryEffect;
    }

    /** The pair's position, or nothing while the portal is unpaired. */
    @Override
    public Set<Position> linkedPositions() {
        return pairPosition == null ? Set.of() : Set.of(pairPosition);
    }
}
