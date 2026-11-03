package model.tile.mechanism;

import model.box.KeyBox;
import model.box.NormalBox;
import model.box.WeakBox;
import model.tile.TileMemento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeyTileTest {

    private final List<Character> activations = new ArrayList<>();
    private final KeyTile key = new KeyTile('a');

    KeyTileTest() {
        key.addListener(activations::add);
    }

    @Test
    @DisplayName("La primera KeyBox activa la llave y avisa a su grupo")
    void keyBoxActivatesTheKey() {
        key.onBoxEntered(new KeyBox());

        assertEquals(List.of('A'), activations);
        assertTrue(key.isUsed());
    }

    @Test
    @DisplayName("Una llave usada no vuelve a usarse")
    void usedKeyIsNotUsedAgain() {
        key.onBoxEntered(new KeyBox());

        key.onBoxEntered(new KeyBox());

        assertEquals(List.of('A'), activations);
    }

    @Test
    @DisplayName("Una NormalBox o una WeakBox no usan la llave")
    void otherBoxesDoNotUseTheKey() {
        key.onBoxEntered(new NormalBox());
        key.onBoxEntered(new WeakBox(3));

        assertTrue(activations.isEmpty());
        assertFalse(key.isUsed());
    }

    @Test
    @DisplayName("Restaurar un memento previo deja la llave sin usar y se puede volver a activar")
    void restoringMakesTheKeyUsableAgain() {
        TileMemento beforeUse = key.save();
        key.onBoxEntered(new KeyBox());

        beforeUse.restore();
        key.onBoxEntered(new KeyBox());

        assertEquals(List.of('A', 'A'), activations);
    }

    @Test
    @DisplayName("Restaurar un memento de la llave usada la deja usada")
    void restoringAUsedKeyKeepsItUsed() {
        key.onBoxEntered(new KeyBox());
        TileMemento afterUse = key.save();

        afterUse.restore();
        key.onBoxEntered(new KeyBox());

        assertEquals(List.of('A'), activations);
        assertTrue(key.isUsed());
    }
}