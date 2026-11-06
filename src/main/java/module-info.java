module sokoban {
    requires javafx.controls;
    requires javafx.media;
    requires com.google.gson;

    opens service to com.google.gson;
    exports app to javafx.graphics;
}
