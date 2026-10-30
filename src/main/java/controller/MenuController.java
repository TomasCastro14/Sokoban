package controller;

import model.game.Game;
import view.Navigator;
import view.Screen;
import view.menus.MenuListener;

import java.util.Objects;

// controller/MenuController.java
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
        if (game.getCurrentLevelNumber() == 0) game.start(1);   // provisorio hasta tener progreso guardado
        navigator.show(Screen.GAME);
    }
    @Override public void onLevels()   { navigator.show(Screen.LEVEL_SELECT); }
    @Override public void onNewGame()  { game.start(1); navigator.show(Screen.GAME); }
    public void onLevelChosen(int level) { game.start(level); navigator.show(Screen.GAME); }
    @Override public void onSettings() { /* TODO: cuando exista SettingsView */ }
    @Override public void onExit()     { exit.run(); }
}
