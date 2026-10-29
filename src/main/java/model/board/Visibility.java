package model.board;

/** Read-only view of what the player has seen. The view draws only visible cells. */
public interface Visibility {

    /** False for hidden cells and for positions outside the board. */
    boolean isVisible(Position position);
}
