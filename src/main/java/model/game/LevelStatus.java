package model.game;

/** The single state of a level: it can never be won and lost at the same time. */
public enum LevelStatus {
    PLAYING,
    WON,
    LOST
}
