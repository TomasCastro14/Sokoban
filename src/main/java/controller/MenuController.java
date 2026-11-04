package controller;

import model.game.Game;
import view.Navigator;
import view.Screen;
import view.menus.MenuListener;

import java.util.Objects;

public final class MenuController implements MenuListener {
    private final Game game;
    private final Navigator navigator;
    private final Runnable exit;

    public MenuController(Game game, Navigator navigator, Runnable exit) {
        this.game = Objects.requireNonNull(game, "game");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.exit = Objects.requireNonNull(exit, "exit");
    }

    @Override public void onContinue() {
        if (!game.hasStarted()) game.start(Game.FIRST_LEVEL);
        navigator.show(Screen.GAME);
    }
    @Override public void onLevels()   { navigator.show(Screen.LEVEL_SELECT); }
    @Override public void onNewGame()  { game.start(Game.FIRST_LEVEL); navigator.show(Screen.GAME); }
    public void onLevelChosen(int level) { game.start(level); navigator.show(Screen.GAME); }
    @Override public void onSettings() { navigator.show(Screen.SETTINGS); }
    @Override public void onExit()     { exit.run(); }
}