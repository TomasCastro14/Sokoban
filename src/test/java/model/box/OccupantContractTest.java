package model.box;

import model.board.Direction;
import model.tile.Floor;
import model.tile.Ice;
import model.tile.Inaccessible;
import model.tile.Portal;
import model.tile.StoragePoint;
import model.tile.Tile;
import model.tile.Wall;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OccupantContractTest {

    /** Every tile except plain floor. */
    private static List<Tile> everyTileButFloor() {
        return List.of(new Ice(), new Wall(), new Inaccessible(), new Portal('a'),
                new StoragePoint(BoxType.NORMAL), new Door('A'), new KeyTile('a'));
    }

    private static List<Tile> everyTile() {
        List<Tile> tiles = new ArrayList<>(everyTileButFloor());
        tiles.add(new Floor());
        return tiles;
    }

    private static List<Occupant> everyOccupant() {
        return List.of(new Player(Direction.DOWN), new NormalBox(), new WeakBox(3), new KeyBox());
    }

    @Test
    @DisplayName("Solo el piso comun acepta una caja expulsada por un portal")
    void onlyFloorAcceptsEjectedBox() {
        assertTrue(new Floor().acceptsEjectedBox());
        for (Tile tile : everyTileButFloor()) {
            assertFalse(tile.acceptsEjectedBox());
        }
    }

    @Test
    @DisplayName("El jugador entra donde el casillero admite jugador; una caja, donde admite esa caja")
    void canEnterUsesTheRuleOfTheTile() {
        Player player = new Player(Direction.DOWN);
        Box box = new NormalBox();
        for (Tile tile : everyTile()) {
            assertEquals(tile.allowsPlayer(), player.canEnter(tile));
            assertEquals(tile.allowsBox(box), box.canEnter(tile));
        }
    }

    @Test
    @DisplayName("Ningun ocupante tira excepcion al preguntarle por cualquier casillero")
    void everyOccupantAnswersForEveryTile() {
        for (Occupant occupant : everyOccupant()) {
            for (Tile tile : everyTile()) {
                assertDoesNotThrow(() -> occupant.canEnter(tile));
                assertDoesNotThrow(() -> occupant.boxIn(tile));
            }
        }
    }

    @Test
    @DisplayName("Al jugador no se lo puede empujar: responde BLOCKED y no cambia nada")
    void playerCannotBePushed() {
        StoragePoint point = new StoragePoint(BoxType.NORMAL);

        assertEquals(PushResult.BLOCKED, new Player(Direction.DOWN).pushOnto(point));
        assertFalse(point.isFilled());
    }

    @Test
    @DisplayName("Una caja empujada a un punto de guardado le avisa al casillero")
    void pushedBoxTellsTheTile() {
        StoragePoint point = new StoragePoint(BoxType.NORMAL);
        Box box = new NormalBox();

        assertEquals(PushResult.PUSHED, box.pushOnto(point));
        assertSame(box, point.getStoredBox());
    }

    @Test
    @DisplayName("Una caja es la caja de su celda; el jugador, la caja guardada debajo (si hay)")
    void boxInReturnsTheBoxOfTheCell() {
        Box box = new NormalBox();
        StoragePoint filled = new StoragePoint(BoxType.NORMAL);
        box.pushOnto(filled);

        assertSame(box, box.boxIn(new Floor()));
        assertSame(box, new Player(Direction.DOWN).boxIn(filled));
        assertNull(new Player(Direction.DOWN).boxIn(new Floor()));
    }
}