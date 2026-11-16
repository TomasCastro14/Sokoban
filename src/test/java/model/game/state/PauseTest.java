package model.game.state;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Game;
import model.game.Level;
import model.game.event.PauseListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PauseTest {

    private static final int LEVEL_COUNT = 5;

    private static Level corridor() {
        return TestLevels.level("########", "#@     #", "#  $.  #", "########");
    }

    private static Game startedGame() {
        Game game = new Game(n -> corridor(), LEVEL_COUNT);
        game.start();
        return game;
    }

    private static final class PauseRecorder implements PauseListener {
        final List<String> events = new ArrayList<>();

        @Override
        public void gamePaused() {
            events.add("paused");
        }

        @Override
        public void gameResumed() {
            events.add("resumed");
        }
    }

    @Test
    @DisplayName("PLAYING -> PAUSED y PAUSED -> PLAYING con togglePause")
    void togglesBetweenPlayingAndPaused() {
        Game game = startedGame();

        game.togglePause();
        assertSame(PausedState.INSTANCE, game.getState());

        game.togglePause();
        assertSame(PlayingState.INSTANCE, game.getState());
    }

    @Test
    @DisplayName("En pausa no corre el tiempo ni se puede mover")
    void pausedStopsTimeAndMoves() {
        Game game = startedGame();
        int remaining = game.getCurrentLevel().getRemainingSeconds();
        game.togglePause();

        game.tick();
        game.move(Direction.RIGHT);

        assertEquals(remaining, game.getCurrentLevel().getRemainingSeconds());
        assertEquals(new Position(1, 1), game.getCurrentLevel().getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Pausar y reanudar avisa a los listeners")
    void pauseIsAnnounced() {
        Game game = startedGame();
        PauseRecorder recorder = new PauseRecorder();
        game.addPauseListener(recorder);

        game.togglePause();
        game.togglePause();

        assertEquals(List.of("paused", "resumed"), recorder.events);
    }

    @Test
    @DisplayName("Reiniciar desde la pausa vuelve a jugar y saca la pausa")
    void restartFromPause() {
        Game game = startedGame();
        PauseRecorder recorder = new PauseRecorder();
        game.addPauseListener(recorder);
        game.togglePause();

        game.restart();

        assertSame(PlayingState.INSTANCE, game.getState());
        assertEquals(List.of("paused", "resumed"), recorder.events);
    }

    @Test
    @DisplayName("En el menu y despues de ganar, ESC no pausa")
    void pauseIsIgnoredOutsidePlaying() {
        Game menu = new Game(n -> corridor(), LEVEL_COUNT);
        menu.togglePause();
        assertSame(MenuState.INSTANCE, menu.getState());

        Game won = new Game(n -> TestLevels.level("######", "#@$. #", "######"), LEVEL_COUNT);
        won.start();
        won.move(Direction.RIGHT);
        won.togglePause();
        assertSame(WonState.INSTANCE, won.getState());
    }
}