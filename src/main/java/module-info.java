module com.sokoban.sokoban {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sokoban.sokoban to javafx.fxml;
    exports com.sokoban.sokoban;
}