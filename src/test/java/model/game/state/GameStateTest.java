package model.game.state;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Game;
import model.game.Level;
import model.game.LevelStats;
import model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private static final int LEVEL_COUNT = 5;

    private static final class FakeContext implements GameContext {
        final List<String> calls = new ArrayList<>();
        GameState stateAfterLevelReacts = PlayingState.INSTANCE;
        boolean undoSucceeds = true;
        boolean lastLevel = false;

        @Override
        public GameState playMove(Direction direction) {
            calls.add("playMove " + direction);
            return stateAfterLevelReacts;
        }

        @Override
        public GameState elapseSecond() {
            calls.add("elapseSecond");
            return stateAfterLevelReacts;
        }

        @Override
        public boolean undoLastMove() {
            calls.add("undoLastMove");
            return undoSucceeds;
        }

        @Override
        public void reloadLevel() {
            calls.add("reloadLevel");
        }

        @Override
        public void loadNextLevel() {
            calls.add("loadNextLevel");
        }

        @Override
        public boolean isLastLevel() {
            return lastLevel;
        }
    }

    private static final LevelStats STATS = new LevelStats();

    private static void assertIgnoresEveryAction(GameState state) {
        FakeContext game = new FakeContext();

        assertSame(state, state.move(game, Direction.RIGHT));
        assertSame(state, state.undo(game));
        assertSame(state, state.restart(game));
        assertSame(state, state.nextLevel(game));
        assertSame(state, state.tick(game));
        assertSame(state, state.onLevelWon(STATS));
        assertSame(state, state.onLevelLost(LossReason.TIME_OUT));
        assertTrue(game.calls.isEmpty(), "no debe pedirle nada al juego: " + game.calls);
    }

    @Nested
    @DisplayName("MenuState")
    class Menu {
        @Test
        @DisplayName("Ninguna acción de juego es válida en el menú")
        void ignoresEverything() {
            assertIgnoresEveryAction(MenuState.INSTANCE);
        }
    }

    @Nested
    @DisplayName("PlayingState")
    class Playing {
        private final FakeContext game = new FakeContext();

        @Test
        @DisplayName("move juega el movimiento y sigue en PLAYING")
        void moveKeepsPlaying() {
            assertSame(PlayingState.INSTANCE, PlayingState.INSTANCE.move(game, Direction.UP));
            assertEquals(List.of("playMove UP"), game.calls);
        }

        @Test
        @DisplayName("move devuelve el estado al que llevó el resultado del nivel")
        void moveReturnsStateLeftByTheLevel() {
            game.stateAfterLevelReacts = WonState.INSTANCE;

            assertSame(WonState.INSTANCE, PlayingState.INSTANCE.move(game, Direction.UP));
        }

        @Test
        @DisplayName("undo deshace la jugada y sigue en PLAYING, aunque no queden undos")
        void undoKeepsPlaying() {
            game.undoSucceeds = false;

            assertSame(PlayingState.INSTANCE, PlayingState.INSTANCE.undo(game));
            assertEquals(List.of("undoLastMove"), game.calls);
        }

        @Test
        @DisplayName("restart recarga el nivel y sigue en PLAYING")
        void restartKeepsPlaying() {
            assertSame(PlayingState.INSTANCE, PlayingState.INSTANCE.restart(game));
            assertEquals(List.of("reloadLevel"), game.calls);
        }

        @Test
        @DisplayName("tick descuenta un segundo")
        void tickElapsesTime() {
            assertSame(PlayingState.INSTANCE, PlayingState.INSTANCE.tick(game));
            assertEquals(List.of("elapseSecond"), game.calls);
        }

        @Test
        @DisplayName("nextLevel no es válido mientras se juega")
        void nextLevelIsIgnored() {
            assertSame(PlayingState.INSTANCE, PlayingState.INSTANCE.nextLevel(game));
            assertTrue(game.calls.isEmpty());
        }

        @Test
        @DisplayName("PLAYING -> WON al ganar el nivel")
        void winGoesToWon() {
            assertSame(WonState.INSTANCE, PlayingState.INSTANCE.onLevelWon(STATS));
        }

        @Test
        @DisplayName("PLAYING -> BLOCKED con un deadlock")
        void deadlockGoesToBlocked() {
            assertSame(BlockedState.INSTANCE, PlayingState.INSTANCE.onLevelLost(LossReason.DEADLOCK));
        }

        @Test
        @DisplayName("PLAYING -> LOST con cualquier derrota no recuperable")
        void otherLossesGoToLost() {
            for (LossReason reason : List.of(LossReason.TIME_OUT, LossReason.BROKEN_BOX,
                    LossReason.WRONG_STORAGE, LossReason.EARLY_KEY_STORAGE)) {
                assertSame(LostState.INSTANCE, PlayingState.INSTANCE.onLevelLost(reason), reason.name());
            }
        }
    }

    @Nested
    @DisplayName("BlockedState")
    class Blocked {
        private final FakeContext game = new FakeContext();

        @Test
        @DisplayName("BLOCKED -> PLAYING con un undo exitoso")
        void undoGoesBackToPlaying() {
            assertSame(PlayingState.INSTANCE, BlockedState.INSTANCE.undo(game));
            assertEquals(List.of("undoLastMove"), game.calls);
        }

        @Test
        @DisplayName("Sin undos disponibles, sigue en BLOCKED")
        void failedUndoStaysBlocked() {
            game.undoSucceeds = false;

            assertSame(BlockedState.INSTANCE, BlockedState.INSTANCE.undo(game));
        }

        @Test
        @DisplayName("BLOCKED -> PLAYING al reiniciar")
        void restartGoesToPlaying() {
            assertSame(PlayingState.INSTANCE, BlockedState.INSTANCE.restart(game));
            assertEquals(List.of("reloadLevel"), game.calls);
        }

        @Test
        @DisplayName("No se puede mover, avanzar de nivel ni corre el tiempo")
        void invalidActions() {
            assertSame(BlockedState.INSTANCE, BlockedState.INSTANCE.move(game, Direction.LEFT));
            assertSame(BlockedState.INSTANCE, BlockedState.INSTANCE.nextLevel(game));
            assertSame(BlockedState.INSTANCE, BlockedState.INSTANCE.tick(game));
            assertTrue(game.calls.isEmpty());
        }
    }

    @Nested
    @DisplayName("LostState")
    class Lost {
        private final FakeContext game = new FakeContext();

        @Test
        @DisplayName("LOST -> PLAYING al reiniciar")
        void restartGoesToPlaying() {
            assertSame(PlayingState.INSTANCE, LostState.INSTANCE.restart(game));
            assertEquals(List.of("reloadLevel"), game.calls);
        }

        @Test
        @DisplayName("No se puede mover, deshacer, avanzar de nivel ni corre el tiempo")
        void invalidActions() {
            assertSame(LostState.INSTANCE, LostState.INSTANCE.move(game, Direction.LEFT));
            assertSame(LostState.INSTANCE, LostState.INSTANCE.undo(game));
            assertSame(LostState.INSTANCE, LostState.INSTANCE.nextLevel(game));
            assertSame(LostState.INSTANCE, LostState.INSTANCE.tick(game));
            assertTrue(game.calls.isEmpty());
        }
    }

    @Nested
    @DisplayName("WonState")
    class Won {
        private final FakeContext game = new FakeContext();

        @Test
        @DisplayName("WON -> PLAYING cargando el siguiente nivel")
        void nextLevelGoesToPlaying() {
            assertSame(PlayingState.INSTANCE, WonState.INSTANCE.nextLevel(game));
            assertEquals(List.of("loadNextLevel"), game.calls);
        }

        @Test
        @DisplayName("WON -> GAME_COMPLETED si era el último nivel")
        void lastLevelGoesToCompleted() {
            game.lastLevel = true;

            assertSame(GameCompletedState.INSTANCE, WonState.INSTANCE.nextLevel(game));
            assertTrue(game.calls.isEmpty());
        }

        @Test
        @DisplayName("WON -> PLAYING al reiniciar")
        void restartGoesToPlaying() {
            assertSame(PlayingState.INSTANCE, WonState.INSTANCE.restart(game));
            assertEquals(List.of("reloadLevel"), game.calls);
        }

        @Test
        @DisplayName("Después de ganar no se puede mover, deshacer ni corre el tiempo")
        void invalidActions() {
            assertSame(WonState.INSTANCE, WonState.INSTANCE.move(game, Direction.LEFT));
            assertSame(WonState.INSTANCE, WonState.INSTANCE.undo(game));
            assertSame(WonState.INSTANCE, WonState.INSTANCE.tick(game));
            assertSame(WonState.INSTANCE, WonState.INSTANCE.onLevelLost(LossReason.TIME_OUT));
            assertTrue(game.calls.isEmpty());
        }
    }

    @Nested
    @DisplayName("GameCompletedState")
    class Completed {
        @Test
        @DisplayName("Estado final: ninguna acción es válida")
        void ignoresEverything() {
            assertIgnoresEveryAction(GameCompletedState.INSTANCE);
        }
    }

    private static Level oneMoveLevel() {
        return TestLevels.level("######", "#@$. #", "######");
    }

    private static Level deadlockLevel() {
        return TestLevels.level("#####", "#   #", "#$  #", "#@ .#", "#####");
    }

    private static Level brokenBoxLevel() {
        return TestLevels.level("#######", "#@1  :#", "#######");
    }

    @Nested
    @DisplayName("Game (integración)")
    class WithGame {

        @Test
        @DisplayName("MENU -> PLAYING -> WON -> ... -> GAME_COMPLETED después del nivel 5")
        void transitionsUntilGameCompleted() {
            Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
            assertSame(MenuState.INSTANCE, game.getState());

            game.start();
            assertSame(PlayingState.INSTANCE, game.getState());

            for (int number = 1; number <= LEVEL_COUNT; number++) {
                game.move(Direction.RIGHT);
                assertSame(WonState.INSTANCE, game.getState(), "level " + number);
                game.nextLevel();
            }

            assertSame(GameCompletedState.INSTANCE, game.getState());
        }

        @Test
        @DisplayName("En el menú, mover no hace nada")
        void moveInMenuIsIgnored() {
            Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);

            game.move(Direction.RIGHT);

            assertSame(MenuState.INSTANCE, game.getState());
        }

        @Test
        @DisplayName("PLAYING -> BLOCKED cuando hay deadlock")
        void deadlockGoesToBlocked() {
            Game game = new Game(n -> deadlockLevel(), LEVEL_COUNT);
            game.start();

            game.move(Direction.UP);

            assertSame(BlockedState.INSTANCE, game.getState());
        }

        @Test
        @DisplayName("PLAYING -> LOST cuando se rompe una caja")
        void brokenBoxGoesToLost() {
            Game game = new Game(n -> brokenBoxLevel(), LEVEL_COUNT);
            game.start();

            game.move(Direction.RIGHT);
            game.move(Direction.RIGHT);

            assertSame(LostState.INSTANCE, game.getState());
        }

        @Test
        @DisplayName("Ganar el nivel N carga el nivel N+1")
        void winningLoadsNextLevel() {
            List<Integer> requested = new ArrayList<>();
            Game game = new Game(n -> {
                requested.add(n);
                return oneMoveLevel();
            }, LEVEL_COUNT);
            game.start();

            game.move(Direction.RIGHT);
            game.nextLevel();

            assertEquals(2, game.getCurrentLevelNumber());
            assertEquals(List.of(1, 2), requested);
            assertSame(PlayingState.INSTANCE, game.getState());
        }

        @Test
        @DisplayName("Desde WON no se permite undo ni mover")
        void undoAndMoveAreIgnoredWhenWon() {
            Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
            game.start();
            game.move(Direction.RIGHT);
            Position player = game.getCurrentLevel().getBoard().getPlayerPosition();

            game.undo();
            game.move(Direction.LEFT);

            assertSame(WonState.INSTANCE, game.getState());
            assertEquals(0, game.getCurrentLevel().getStats().getUndos());
            assertEquals(player, game.getCurrentLevel().getBoard().getPlayerPosition());
        }

        @Test
        @DisplayName("El tiempo no corre fuera de PLAYING")
        void timeStopsAfterWinning() {
            Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
            game.start();
            game.move(Direction.RIGHT);
            int remaining = game.getCurrentLevel().getRemainingSeconds();

            game.tick();

            assertEquals(remaining, game.getCurrentLevel().getRemainingSeconds());
        }

        @Test
        @DisplayName("Reiniciar reconstruye el nivel desde el .txt, como si fuera nuevo")
        void restartRebuildsLevel() {
            List<Integer> requested = new ArrayList<>();
            Game game = new Game(n -> {
                requested.add(n);
                return TestLevels.level("#######", "#@ $ .#", "#######");
            }, LEVEL_COUNT);
            game.start();
            Level original = game.getCurrentLevel();
            game.move(Direction.RIGHT);
            game.move(Direction.RIGHT);

            game.restart();

            assertNotSame(original, game.getCurrentLevel());
            assertEquals(List.of(1, 1), requested);
            assertEquals(new Position(1, 1), game.getCurrentLevel().getBoard().getPlayerPosition());
            assertEquals(0, game.getCurrentLevel().getStats().getMoves());
            assertSame(PlayingState.INSTANCE, game.getState());
        }

        @Test
        @DisplayName("Reiniciar después de perder vuelve a PLAYING")
        void restartAfterLosingPlaysAgain() {
            Game game = new Game(n -> brokenBoxLevel(), LEVEL_COUNT);
            game.start();
            game.move(Direction.RIGHT);
            game.move(Direction.RIGHT);

            game.restart();

            assertSame(PlayingState.INSTANCE, game.getState());
        }
    }
}