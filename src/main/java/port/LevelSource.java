package port;

import exception.LevelNotFoundException;

import java.util.List;

/**
 * Protected Variations: where levels come from (classpath, a user file, memory) is hidden here.
 * Contract: the raw lines of the level, in order, without line terminators.
 */
public interface LevelSource {

    List<String> lines(int levelNumber) throws LevelNotFoundException;

    boolean exists(int levelNumber);
}
