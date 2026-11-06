module com.sokoban.sokoban {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires com.google.gson;

    opens com.sokoban.sokoban to javafx.fxml;
    opens service to com.google.gson;
    exports com.sokoban.sokoban;
}
