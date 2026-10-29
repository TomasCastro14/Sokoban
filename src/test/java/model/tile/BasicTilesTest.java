package model.tile;

import model.box.NormalBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasicTilesTest {

    @Test
    void wallDoesNotAllowPlayer() {
        assertFalse(new Wall().allowsPlayer());
    }

    @Test
    void wallDoesNotAllowBox() {
        assertFalse(new Wall().allowsBox(new NormalBox()));
    }

    @Test
    void inaccessibleDoesNotAllowPlayer() {
        assertFalse(new Inaccessible().allowsPlayer());
    }

    @Test
    void inaccessibleDoesNotAllowBox() {
        assertFalse(new Inaccessible().allowsBox(new NormalBox()));
    }

    @Test
    void floorAllowsPlayer() {
        assertTrue(new Floor().allowsPlayer());
    }

    @Test
    void floorAllowsBox() {
        assertTrue(new Floor().allowsBox(new NormalBox()));
    }

    static Stream<Arguments> tilesSeenThroughTheAbstraction() {
        return Stream.of(
                Arguments.of(new Wall(), false, false),
                Arguments.of(new Inaccessible(), false, false),
                Arguments.of(new Floor(), true, true));
    }

    /** Polymorphism: callers only know Tile, never the concrete type. */
    @ParameterizedTest
    @MethodSource("tilesSeenThroughTheAbstraction")
    void eachTileAnswersForItself(Tile tile, boolean player, boolean box) {
        assertEquals(player, tile.allowsPlayer());
        assertEquals(box, tile.allowsBox(new NormalBox()));
    }
}
