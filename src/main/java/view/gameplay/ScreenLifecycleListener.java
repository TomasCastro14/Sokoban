package view.gameplay;

import javafx.scene.Scene;

public interface ScreenLifecycleListener {
    void screenShown(Scene scene);
    void screenHidden();
}
