package com.sokoban.sokoban;

import controller.GameClock;
import controller.GamePresenter;
import controller.InputController;
import controller.LevelProgression;
import controller.MenuController;
import factory.LevelLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.game.Game;
import model.game.Level;
import service.JsonProgressRepository;
import service.ProgressSaver;
import view.GameWindow;
import view.Screen;
import view.SpriteCatalog;
import view.gameplay.GameView;
import view.menus.LevelSelectView;
import view.menus.LevelSelectView.LevelInfo;
import view.menus.MenuView;

import java.nio.file.Path;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class HelloApplication extends Application {
    private static final int LEVEL_COUNT = 5;
    private static final double UI_FONT_SIZE = 14;
    private static final int NO_STARS = 0;
    private static final String UI_FONT = "/assets/Kenney Future.ttf";
    private static final Runnable NOTHING = () -> { };
    private static final String PROGRESS_FOLDER = ".new-sokoban";
    private static final String PROGRESS_FILE = "progress.json";

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream(UI_FONT), UI_FONT_SIZE);
        SpriteCatalog sprites = new SpriteCatalog();

        GameWindow window = new GameWindow(stage, sprites);
        Game game = new Game(levelsFrom(new LevelLoader()), LEVEL_COUNT);
        GameView gameView = new GameView(sprites);
        GamePresenter presenter = new GamePresenter(game, gameView, new InputController(game), new GameClock(game));
        MenuController menus = new MenuController(game, window, Platform::exit);
        new LevelProgression(game, window, Platform::runLater);
        game.addLevelCompletedListener(new ProgressSaver(new JsonProgressRepository(progressFile()), LEVEL_COUNT));

        window.register(Screen.MAIN_MENU, new MenuView(menus).getRoot(), NOTHING);
        window.register(Screen.LEVEL_SELECT,
                new LevelSelectView(levelInfos(), menus::onLevelChosen, LevelSelectView.FIRST_INDEX, window::back)
                        .getRoot(),
                window::back);
        window.register(Screen.GAME, gameView.getRoot(), presenter::onEscape);

        window.show(Screen.MAIN_MENU);
        window.open();
    }

    /** Levels are read from the classpath; progress is written outside the jar, in the user's home. */
    private static Path progressFile() {
        return Path.of(System.getProperty("user.home"), PROGRESS_FOLDER, PROGRESS_FILE);
    }

    private static List<LevelInfo> levelInfos() {
        return IntStream.rangeClosed(Game.FIRST_LEVEL, LEVEL_COUNT)
                .mapToObj(n -> new LevelInfo("Nivel " + n, NO_STARS, true))
                .toList();
    }

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