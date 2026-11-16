package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import model.board.Position;

import java.util.List;
import java.util.Map;

/** Each portal letter must appear exactly twice, so every portal has exactly one pair. */
class PortalPairRule implements LevelRule {
    private static final int PORTALS_PER_PAIR = 2;

    @Override
    public void check(LevelBuilder level) throws InvalidLevelException {
        for (Map.Entry<Character, List<Position>> letter : level.portalsByLetter().entrySet()) {
            List<Position> portals = letter.getValue();
            if (portals.size() != PORTALS_PER_PAIR) {
                throw new InvalidLevelException(LevelFormatRule.PORTAL_NOT_PAIRED,
                        level.lineOf(portals.get(0)), describe(letter.getKey(), portals.size()));
            }
        }
    }

    private static String describe(char letter, int times) {
        return "'" + letter + "' aparece " + times + (times == 1 ? " vez" : " veces");
    }
}
