package controller;

import model.game.Game;
import model.game.progress.Progress;
import view.Navigator;
import view.Screen;
import view.menus.MenuListener;

import java.util.Objects;

/**
 * Every way into a level starts it from its .txt file: a level left halfway is never resumed.
 * "Continue" means "play the last unlocked level", from the beginning.
 */
public final class MenuController implements MenuListener {
    private final Game game;
    private final Progress progress;
    private final Navigator navigator;
    private final Runnable exit;

    public MenuController(Game game, Progress progress, Navigator navigator, Runnable exit) {
        this.game = Objects.requireNonNull(game, "game");
        this.progress = Objects.requireNonNull(progress, "progress");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.exit = Objects.requireNonNull(exit, "exit");
    }

    @Override public void onContinue() { play(progress.getLastUnlockedLevel()); }
    @Override public void onLevels()   { navigator.show(Screen.LEVEL_SELECT); }
    @Override public void onNewGame()  { play(Game.FIRST_LEVEL); }
    @Override public void onSettings() { navigator.show(Screen.SETTINGS); }
    @Override public void onExit()     { exit.run(); }

    /** A locked level cannot be played; the view already disables it, this is the guard behind it. */
    public void onLevelChosen(int level) {
        if (progress.isUnlocked(level)) {
            play(level);
        }
    }

    private void play(int level) {
        game.start(level);
        navigator.show(Screen.GAME);
    }
}
