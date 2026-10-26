package model.board;

import factory.TestLevels;
import model.box.Player;
import model.game.Level;
import model.tile.Floor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class MovementTest {

    private static Level openRoom() {
        return TestLevels.level(
                "#######",
                "#     #",
                "#  @  #",
                "#     #",
                "#  $. #",
                "#######");
    }

    @Test
    @DisplayName("1. El jugador se mueve 1 celda en las 4 direcciones si el destino es piso")
    void movesOneCellInEachDirection() {
        Position start = new Position(2, 3);

        for (Direction direction : Direction.values()) {
            Level level = openRoom();

            assertEquals(MoveResult.OK, level.move(direction), direction.name());
            assertEquals(start.move(direction), level.getBoard().getPlayerPosition(), direction.name());
        }
    }

    @Test
    @DisplayName("2. El jugador no se mueve contra una pared")
    void doesNotMoveIntoWall() {
        Level level = TestLevels.level(
                "######",
                "#@$. #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.UP));
        assertEquals(MoveResult.BLOCKED, level.move(Direction.LEFT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. El jugador no se mueve contra terreno no accesible (%)")
    void doesNotMoveIntoVoid() {
        Level level = TestLevels.level(
                "######",
                "#@%  #",
                "# $. #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. No existe movimiento en diagonal")
    void thereAreNoDiagonalDirections() {
        assertEquals(EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT),
                EnumSet.allOf(Direction.class));
        for (Direction direction : Direction.values()) {
            int distance = Math.abs(direction.getRowOffset()) + Math.abs(direction.getColOffset());
            assertEquals(1, distance, direction.name());
        }
    }

    @Test
    @DisplayName("4. El jugador no puede pisar un punto de guardado vacio (es un agujero)")
    void cannotStepOnEmptyStoragePoint() {
        Level level = TestLevels.level(
                "######",
                "#@.  #",
                "#  $ #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("5. El jugador puede pasar sobre un punto de guardado con una caja encima (puente)")
    void canWalkOverStoredBox() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#   $.#",
                "#######");

        level.move(Direction.RIGHT); // stores the box at (1,3)
        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)), "the stored box stays as a bridge");
    }

    @Test
    @DisplayName("6. Moverse sin empujar suma un movimiento y no suma empuje")
    void walkingCountsMoveButNotPush() {
        Level level = openRoom();

        level.move(Direction.UP);

        assertEquals(1, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6b. Caminar a piso libre en cada direccion da OK, suma 1 movimiento y ningun empuje")
    void walkingInEachDirectionCountsOneMove() {
        for (Direction direction : Direction.values()) {
            Level level = openRoom();

            assertEquals(MoveResult.OK, level.move(direction), direction.name());

            assertEquals(1, level.getStats().getMoves(), direction.name());
            assertEquals(0, level.getStats().getPushes(), direction.name());
        }
    }

    @Test
    @DisplayName("6c. N pasos seguidos sin empujar suman N movimientos y 0 empujes")
    void severalWalksCountEachMove() {
        Level level = openRoom();

        level.move(Direction.UP);
        level.move(Direction.LEFT);
        level.move(Direction.LEFT);
        level.move(Direction.DOWN);

        assertEquals(new Position(2, 1), level.getBoard().getPlayerPosition());
        assertEquals(4, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6d. Chocar contra una pared no suma movimientos ni empujes")
    void walkingIntoWallCountsNothing() {
        Level level = TestLevels.level(
                "######",
                "#@ $.#",
                "######");

        level.move(Direction.UP);
        level.move(Direction.LEFT);

        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6e. Chocar contra terreno no accesible (%) no suma movimientos ni empujes")
    void walkingIntoVoidCountsNothing() {
        Level level = TestLevels.level(
                "######",
                "#@%  #",
                "# $. #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6f. Una caja que no se puede mover bloquea al jugador y no suma nada")
    void unmovableBoxCountsNothing() {
        Level level = TestLevels.level(
                "######",
                "#@$#.#",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6g. Empujar una caja da PUSHED y suma 1 movimiento y 1 empuje")
    void pushingCountsMoveAndPush() {
        Level level = TestLevels.level(
                "#######",
                "#@$  .#",
                "#######");

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertEquals(1, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6h. Caminar y empujar mezclados: los movimientos cuentan todo, los empujes solo los empujes")
    void walkingAndPushingAreCountedApart() {
        Level level = TestLevels.level(
                "########",
                "#@ $  .#",
                "#      #",
                "########");

        level.move(Direction.RIGHT); // walk
        level.move(Direction.RIGHT); // push
        level.move(Direction.DOWN);  // walk

        assertEquals(3, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
    }

    private static void assertBlockedInEveryDirection(String wall) {
        for (Direction direction : Direction.values()) {
            Level level = TestLevels.level(
                    wall.repeat(3),
                    wall + "@" + wall,
                    wall.repeat(3));

            assertEquals(MoveResult.BLOCKED, level.move(direction), direction.name());
            assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition(), direction.name());
            assertEquals(0, level.getStats().getMoves(), direction.name());
            assertEquals(0, level.getStats().getPushes(), direction.name());
        }
    }

    @Test
    @DisplayName("7. Una pared (#) bloquea en las 4 direcciones, sin mover ni contar")
    void wallBlocksInEveryDirection() {
        assertBlockedInEveryDirection("#");
    }

    @Test
    @DisplayName("7. Terreno no accesible (%) bloquea en las 4 direcciones, sin mover ni contar")
    void voidBlocksInEveryDirection() {
        assertBlockedInEveryDirection("%");
    }

    @Test
    @DisplayName("8. Moverse a piso libre al lado de una pared sigue dando OK y suma 1 movimiento")
    void walkingBesideAWallStillWorks() {
        Level level = TestLevels.level(
                "######",
                "#@   #",
                "######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("9. Un destino fuera de la grilla da BLOCKED, igual que una pared")
    void leavingTheGridIsBlocked() {
        Position only = new Position(0, 0);
        Cell cell = new Cell(only, new Floor());
        cell.place(new Player(Direction.DOWN));
        Board board = new Board(new Cell[][] {{cell}}, only);

        for (Direction direction : Direction.values()) {
            assertEquals(MoveResult.BLOCKED, board.movePlayer(direction), direction.name());
            assertEquals(only, board.getPlayerPosition(), direction.name());
        }
    }
}
