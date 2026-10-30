package com.sokoban.sokoban;

import controller.GameClock;
import controller.GamePresenter;
import controller.InputController;
import controller.MenuController;
import factory.LevelLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.game.Game;
import model.game.Level;
import view.GameWindow;
import view.Screen;
import view.SpriteCatalog;
import view.gameplay.GameView;
import view.menus.LevelSelectView;
import view.menus.LevelSelectView.LevelInfo;
import view.menus.MenuView;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/** Composition root: the only place that knows every piece and wires them together. */
public class HelloApplication extends Application {
    private static final int LEVEL_COUNT = 5;

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/assets/Kenney Future.ttf"), 14);
        SpriteCatalog sprites = new SpriteCatalog();

        GameWindow window = new GameWindow(stage, sprites);
        Game game = new Game(levelsFrom(new LevelLoader()), LEVEL_COUNT);
        GameView gameView = createGameView(game, sprites);
        MenuController menus = new MenuController(game, window, Platform::exit);

        window.register(Screen.MAIN_MENU, new MenuView(menus).getRoot());
        window.register(Screen.LEVEL_SELECT,
                new LevelSelectView(levelInfos(), menus::onLevelChosen, 0, window::back).getRoot());
        window.register(Screen.GAME, gameView.getRoot());

        window.show(Screen.MAIN_MENU);
        window.open();
    }

    /** The view is passive: the presenter is what joins it to the model, the keyboard and the clock. */
    private static GameView createGameView(Game game, SpriteCatalog sprites) {
        GameView gameView = new GameView(sprites);
        new GamePresenter(game, gameView, new InputController(game), new GameClock(game));
        return gameView;
    }

    /** Provisional: every level unlocked and no stars until saved progress exists. */
    private static List<LevelInfo> levelInfos() {
        return IntStream.rangeClosed(1, LEVEL_COUNT)
                .mapToObj(n -> new LevelInfo("Nivel " + n, 0, true))
                .toList();
    }

    /** LevelLoader.load throws checked exceptions; Game asks for an IntFunction. */
    private static IntFunction<Level> levelsFrom(LevelLoader loader) {
        return number -> {
            try {
                return loader.load(number);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "No se pudo cargar el nivel " + number + ": " + e.getMessage(), e);
            }
        };
    }
}