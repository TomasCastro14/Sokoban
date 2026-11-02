package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SokobanApp extends Application {
    private static final String TITLE = "Sokoban";

    @Override
    public void start(Stage stage) {
        stage.setTitle(TITLE);
        stage.setScene(new Scene(new StackPane()));
        stage.show();
    }
}