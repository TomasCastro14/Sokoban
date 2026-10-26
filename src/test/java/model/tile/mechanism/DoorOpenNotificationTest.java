package model.tile.mechanism;

import model.board.ReachabilityListener;
import model.box.KeyBox;
import model.box.NormalBox;
import model.box.WeakBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** The door group tells its listeners once per opening, after the doors changed state. */
class DoorOpenNotificationTest {

    private KeyTile keyA;
    private Door doorA1;
    private Door doorA2;
    private DoorGroup groupA;

    /** Records, for each notice, whether every door of the group was already open when it arrived. */
    private static class Recorder implements ReachabilityListener {
        private final List<Door> doors;
        private int notices;
        private boolean doorsWereOpen = true;

        Recorder(List<Door> doors) {
            this.doors = doors;
        }

        @Override
        public void reachabilityChanged() {
            notices++;
            doorsWereOpen &= doors.stream().allMatch(Door::isOpen);
        }
    }

    @BeforeEach
    void setUp() {
        keyA = new KeyTile('a');
        doorA1 = new Door('A');
        doorA2 = new Door('A');
        groupA = new DoorGroup('A');
        groupA.addDoor(doorA1);
        groupA.addDoor(doorA2);
        keyA.addListener(groupA);
    }

    @Test
    @DisplayName("Una KeyBox sobre la baldosa abre las puertas y el listener recibe el aviso")
    void keyBoxOpensDoorsAndListenerIsTold() {
        Recorder listener = new Recorder(List.of(doorA1, doorA2));
        groupA.addReachabilityListener(listener);

        keyA.onBoxEntered(new KeyBox());

        assertEquals(1, listener.notices);
        assertTrue(listener.doorsWereOpen, "the notice comes after the doors changed state");
    }

    @Test
    @DisplayName("Varias puertas del mismo grupo se abren a la vez con un solo aviso")
    void severalDoorsOneNotice() {
        Recorder listener = new Recorder(List.of(doorA1, doorA2));
        groupA.addReachabilityListener(listener);

        keyA.onBoxEntered(new KeyBox());

        assertTrue(doorA1.isOpen());
        assertTrue(doorA2.isOpen());
        assertEquals(1, listener.notices);
    }

    @Test
    @DisplayName("Una NormalBox o una WeakBox sobre la baldosa no avisa nada")
    void boxesThatDoNotOpenDoorsSendNoNotice() {
        Recorder listener = new Recorder(List.of(doorA1, doorA2));
        groupA.addReachabilityListener(listener);

        keyA.onBoxEntered(new NormalBox());
        keyA.onBoxEntered(new WeakBox(3));

        assertEquals(0, listener.notices);
        assertFalse(doorA1.isOpen());
    }

    @Test
    @DisplayName("La baldosa a no dispara avisos del grupo B")
    void keyTileAIsSilentForGroupB() {
        Door doorB = new Door('B');
        DoorGroup groupB = new DoorGroup('B');
        groupB.addDoor(doorB);
        keyA.addListener(groupB);
        Recorder listenerB = new Recorder(List.of(doorB));
        groupB.addReachabilityListener(listenerB);

        keyA.onBoxEntered(new KeyBox());

        assertEquals(0, listenerB.notices);
        assertFalse(doorB.isOpen());
    }

    @Test
    @DisplayName("Con varios listeners suscriptos, todos reciben el aviso")
    void everyListenerIsTold() {
        List<Door> doors = List.of(doorA1, doorA2);
        Recorder first = new Recorder(doors);
        Recorder second = new Recorder(doors);
        groupA.addReachabilityListener(first);
        groupA.addReachabilityListener(second);

        keyA.onBoxEntered(new KeyBox());

        assertEquals(1, first.notices);
        assertEquals(1, second.notices);
    }
}
