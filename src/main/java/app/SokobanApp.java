package app;

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
import view.components.buttons.ButtonFactory;
import view.components.container.PopupLayout;
import view.components.images.ImageLoader;
import view.components.images.UiImages;
import view.gameplay.GameView;
import view.menus.LevelSelectView;
import view.menus.LevelSelectView.LevelInfo;
import view.menus.MenuView;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class SokobanApp extends Application {
    private static final int LEVEL_COUNT = 5;
    private static final String FONT_PATH = "/assets/Kenney Future.ttf";
    private static final double FONT_SIZE = 14;

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
        UiImages images = new UiImages(new ImageLoader());
        ButtonFactory buttons = new ButtonFactory(images);
        PopupLayout popups = new PopupLayout(buttons);
        SpriteCatalog sprites = new SpriteCatalog(images);

        GameWindow window = new GameWindow(stage, sprites);
        Game game = new Game(levelsFrom(new LevelLoader()), LEVEL_COUNT);
        GameView gameView = createGameView(game, sprites);
        MenuController menus = new MenuController(game, window, Platform::exit);

        window.register(Screen.MAIN_MENU, new MenuView(menus, buttons).getRoot());
        window.register(Screen.LEVEL_SELECT,
                new LevelSelectView(levelInfos(), menus::onLevelChosen, 0, window::back,
                        images, buttons, popups).getRoot());
        window.register(Screen.GAME, gameView.getRoot());

        window.show(Screen.MAIN_MENU);
        window.open();
    }

    private static GameView createGameView(Game game, SpriteCatalog sprites) {
        GameView gameView = new GameView(sprites);
        new GamePresenter(game, gameView, new InputController(game), new GameClock(game));
        return gameView;
    }

    private static List<LevelInfo> levelInfos() {
        return IntStream.rangeClosed(1, LEVEL_COUNT)
                .mapToObj(n -> new LevelInfo("Nivel " + n, 0, true))
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