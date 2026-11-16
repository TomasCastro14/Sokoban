package factory;

import exception.InvalidLevelException;

/** One format rule a level must follow. The builder checks every rule before assembling the level. */
interface LevelRule {

    void check(LevelBuilder level) throws InvalidLevelException;
}
