package com.sokoban.sokoban;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import view.*;

import java.awt.*;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/assets/Kenney Future.ttf"), 14);
        GameWindow window = new GameWindow(stage);

        window.register(Screen.LEVEL_SELECT, new LevelSelectView(List.of(
                new LevelSelectView.LevelInfo("Lunes", 1, true),
                new LevelSelectView.LevelInfo("Martes", 2, true),
                new LevelSelectView.LevelInfo("Miércoles", 0, false)),
                n -> { /* arrancar nivel n */ }, 0).getRoot());

        window.register(Screen.MAIN_MENU, new MenuView(
                () -> window.show(Screen.LEVEL_SELECT),   // CONTINUAR (provisorio: último nivel desbloqueado)
                () -> window.show(Screen.LEVEL_SELECT),   // NIVELES
                () -> { /* nueva partida */ },
                Platform::exit).getRoot());

        window.show(Screen.MAIN_MENU);
        window.open();
    }
}
