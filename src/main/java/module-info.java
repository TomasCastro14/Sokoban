module com.sokoban.sokoban {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.sokoban.sokoban to javafx.fxml;
    exports com.sokoban.sokoban;
}