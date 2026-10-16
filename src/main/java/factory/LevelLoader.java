package factory;

import exception.InvalidLevelException;
import model.game.Level;

public class LevelLoader {

    /** Loads a level from the classpath, e.g. "/levels/nivel01.txt". */
    public Level load(String resourcePath) throws InvalidLevelException {
        throw new UnsupportedOperationException("TODO");
    }

    /** Parses the full text of a level file: header, "---" separator and map. */
    public Level parse(String text) throws InvalidLevelException {
        throw new UnsupportedOperationException("TODO");
    }
}
