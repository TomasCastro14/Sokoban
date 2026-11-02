package controller;

import model.game.Game;
import view.Navigator;
import view.Screen;
import view.menus.MenuListener;

import java.util.Objects;

public final class MenuController implements MenuListener {
    private static final int FIRST_LEVEL = 1;
    private static final int NO_LEVEL = 0;

    private final Game game;
    private final Navigator navigator;
    private final Runnable exit;

    public MenuController(Game game, Navigator navigator, Runnable exit) {
        this.game = Objects.requireNonNull(game, "game");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.exit = Objects.requireNonNull(exit, "exit");
    }

    @Override
    public void onContinue() {
        if (game.getCurrentLevelNumber() == NO_LEVEL) {
            game.start(FIRST_LEVEL);
        }
        navigator.show(Screen.GAME);
    }

    @Override
    public void onLevels() {
        navigator.show(Screen.LEVEL_SELECT);
    }

    @Override
    public void onNewGame() {
        startLevel(FIRST_LEVEL);
    }

    public void onLevelChosen(int level) {
        startLevel(level);
    }

    @Override
    public void onSettings() {
    }

    @Override
    public void onExit() {
        exit.run();
    }

    private void startLevel(int level) {
        game.start(level);
        navigator.show(Screen.GAME);
    }
}