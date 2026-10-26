package com.sokoban.sokoban;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import view.*;
import view.menus.LevelSelectView;
import view.menus.MenuView;

import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/assets/Kenney Future.ttf"), 14);
        GameWindow window = new GameWindow(stage);

        window.register(Screen.LEVEL_SELECT, new LevelSelectView(
            List.of(
                new LevelSelectView.LevelInfo("Lunes", 1, true),
                new LevelSelectView.LevelInfo("Martes", 2, true),
                new LevelSelectView.LevelInfo("Miércoles", 0, false)),
            n -> { /* arrancar nivel n */ }, 0, window::back).getRoot());

        window.register(Screen.MAIN_MENU, new MenuView(
            () -> { /* nueva partida */ },   // CONTINUAR (provisorio: último nivel desbloqueado)
            () -> window.show(Screen.LEVEL_SELECT),   // NIVELES
            () -> { /* nueva partida */ },
            Platform::exit).getRoot());

        window.show(Screen.MAIN_MENU);
        window.open();
    }
}
