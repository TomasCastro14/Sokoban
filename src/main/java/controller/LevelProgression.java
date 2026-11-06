package controller;

import model.game.Game;
import model.game.event.GameCompletionListener;
import view.Navigator;
import view.Screen;

import java.util.Objects;

/** After the last level is won the game is over and the player goes back to the main menu. */
public final class LevelProgression implements GameCompletionListener {
    private final Navigator navigator;

    public LevelProgression(Game game, Navigator navigator) {
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        Objects.requireNonNull(game, "game").addCompletionListener(this);
    }

    @Override
    public void gameCompleted() {
        navigator.show(Screen.MAIN_MENU);
    }
}
