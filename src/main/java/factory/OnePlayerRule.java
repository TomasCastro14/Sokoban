package factory;

import exception.InvalidLevelException;
import model.box.Player;

/** The level must have exactly one player. It is not tied to a single line of the file. */
class OnePlayerRule implements LevelRule {
    private static final int NO_LINE = -1;

    @Override
    public void check(LevelBuilder level) throws InvalidLevelException {
        long players = level.contents().stream().filter(c -> c.occupant() instanceof Player).count();
        if (players != 1) {
            throw new InvalidLevelException("The level must have exactly one player, found " + players, NO_LINE);
        }
    }
}
