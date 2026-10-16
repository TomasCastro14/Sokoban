package com.sokoban.sokoban;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import view.*;

import java.io.IOException;

public class HelloApplication extends Application {
    // HelloApplication.start (provisorio, hasta que haya controlador)
    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/assets/Kenney Future.ttf"), 14);
        GameWindow window = new GameWindow(stage);
        window.register(Screen.MAIN_MENU,
                new MenuView(() -> {}, () -> {}, javafx.application.Platform::exit).getRoot());
        window.show(Screen.MAIN_MENU);
        window.open();
    }
}
