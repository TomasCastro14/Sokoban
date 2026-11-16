package model.game.event;

import model.board.Direction;
import model.board.Visibility;

/** Observer de los cambios del tablero durante una jugada. */
public interface BoardListener {

    /** El jugador avanzó una jugada en la dirección indicada (aunque haya patinado varias celdas). */
    void playerMoved(Direction direction);

    /** El jugador empujó una caja en la dirección indicada. */
    void boxPushed(Direction direction);

    /** Una caja quedó guardada en un objetivo. */
    void boxStored(int storedBoxes, int totalBoxes);

    /** Se abrieron puertas en la jugada y se recalculó la zona visible. */
    void doorOpened(Visibility visibility);
}