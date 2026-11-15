package controller;

import model.game.Game;
import model.game.event.GameCompletionListener;
import view.Navigator;
import view.Screen;

import java.util.Objects;

public final class LevelProgression implements GameCompletionListener {
    private final Navigator navigator;

    private LevelProgression(Navigator navigator) {
        this.navigator = Objects.requireNonNull(navigator, "navigator");
    }

    public static LevelProgression attach(Game game, Navigator navigator) {
        LevelProgression progression = new LevelProgression(navigator);
        Objects.requireNonNull(game, "game").addCompletionListener(progression);
        return progression;
    }

    @Override
    public void gameCompleted() {
        navigator.show(Screen.MAIN_MENU);
    }
}
