package model.board;

/** Told when something on the board changed which cells the player can reach (e.g. a door opened). */
public interface ReachabilityListener {

    void reachabilityChanged();
}
