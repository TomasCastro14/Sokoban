package app;

import controller.GameClock;
import controller.GamePresenter;
import controller.InputController;
import controller.LevelDialogPresenter;
import controller.LevelProgression;
import controller.MenuController;
import controller.PlaySession;
import controller.SettingsPresenter;
import controller.SoundEventListener;
import factory.LevelLoader;
import factory.StandardLossRules;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.game.Game;
import model.game.Level;
import model.game.score.StandardScoreCalculator;
import service.JsonProgressRepository;
import service.ProgressSaver;
import service.sound.JavaFxSoundService;
import service.sound.SilentSoundService;
import service.sound.SoundService;
import view.GameWindow;
import view.Screen;
import view.SpriteCatalog;
import view.components.buttons.ButtonFactory;
import view.components.buttons.ButtonProvider;
import view.components.container.PopupInstaller;
import view.components.container.PopupLayout;
import view.components.images.ImageLoader;
import view.components.images.ImageProvider;
import view.components.images.UiImages;
import view.gameplay.GameView;
import view.gameplay.dialog.LevelDialogsView;
import view.menus.LevelSelectView;
import view.menus.LevelSelectView.LevelInfo;
import view.menus.MenuView;
import view.menus.SettingsView;

import java.lang.System.Logger;
import java.nio.file.Path;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/** Composition root: the only place that knows every piece and wires them together. */
public class SokobanApp extends Application {
    private static final int LEVEL_COUNT = 5;
    private static final String FONT_PATH = "/assets/Kenney Future.ttf";
    private static final double FONT_SIZE = 14;
    private static final String PROGRESS_FOLDER = ".new-sokoban";
    private static final String PROGRESS_FILE = "progress.json";
    private static final Runnable NO_ESCAPE = () -> { };
    private static final Logger LOG = System.getLogger(SokobanApp.class.getName());

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
        ImageProvider images = new UiImages(new ImageLoader());
        ButtonProvider buttons = new ButtonFactory(images);
        PopupInstaller popups = new PopupLayout(buttons);
        SpriteCatalog sprites = new SpriteCatalog(images);

        GameWindow window = new GameWindow(stage, sprites);
        Game game = new Game(levelsFrom(new LevelLoader(StandardLossRules.create())), LEVEL_COUNT);
        saveProgressOf(game);
        SoundService sound = createSoundService();
        playSoundsOf(game, sound);
        LevelDialogsView dialogs = new LevelDialogsView(images, buttons);
        GameView gameView = new GameView(sprites, dialogs.getRoot());
        PlaySession session = new PlaySession(new InputController(game), new GameClock(game));
        GamePresenter gamePresenter = new GamePresenter(game, gameView, session);
        new LevelDialogPresenter(game, dialogs, session, new StandardScoreCalculator(), window);
        new LevelProgression(game, window);
        MenuController menus = new MenuController(game, window, Platform::exit);

        window.register(Screen.MAIN_MENU, new MenuView(menus, buttons).getRoot(), NO_ESCAPE);
        window.register(Screen.LEVEL_SELECT,
                new LevelSelectView(levelInfos(), menus::onLevelChosen, LevelSelectView.FIRST_INDEX, window::back,
                        images, buttons, popups).getRoot(), window::back);
        window.register(Screen.GAME, gameView.getRoot(), gamePresenter::onEscape);
        window.register(Screen.SETTINGS,
                new SettingsView(new SettingsPresenter(sound), window::back, popups).getRoot(), window::back);

        window.show(Screen.MAIN_MENU);
        window.open();
    }

    /** Levels are read from the classpath; progress is written outside the .jar, in the user's home. */
    private static void saveProgressOf(Game game) {
        Path file = Path.of(System.getProperty("user.home"), PROGRESS_FOLDER, PROGRESS_FILE);
        ProgressSaver saver = new ProgressSaver(new JsonProgressRepository(file), LEVEL_COUNT);
        game.addLevelCompletedListener(saver);
    }

    private static SoundService createSoundService() {
        try {
            return new JavaFxSoundService();
        } catch (RuntimeException | LinkageError e) {
            LOG.log(Logger.Level.WARNING, "Audio is not available, the game continues without sound", e);
            return new SilentSoundService();
        }
    }

    private static void playSoundsOf(Game game, SoundService sound) {
        SoundEventListener listener = new SoundEventListener(sound);
        game.addBoardListener(listener);
        game.addLevelFlowListener(listener);
        game.addLevelOutcomeListener(listener);
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