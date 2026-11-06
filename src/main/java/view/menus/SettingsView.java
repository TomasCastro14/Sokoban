package view.menus;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import service.sound.Volume;
import view.components.container.PopupLayout;

import java.util.Objects;

public class SettingsView {
    private static final double SPACING = 16;
    private static final String VOLUME_LABEL = "VOLUMEN";
    private static final String MUTE_LABEL = "SILENCIAR";

    private final StackPane root = new StackPane();

    public SettingsView(SettingsListener listener, Runnable onClose, PopupLayout popups) {
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(popups, "popups");

        VBox panel = new VBox(SPACING, new Label(VOLUME_LABEL), volumeSlider(listener), muteCheckBox(listener));
        panel.getStyleClass().add("panel");
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane content = new StackPane(panel);
        popups.install(root, content, onClose);
    }

    private static Slider volumeSlider(SettingsListener listener) {
        Slider slider = new Slider(Volume.MIN, Volume.MAX, listener.currentVolume());
        slider.valueProperty().addListener((property, previous, level) -> listener.volumeChanged(level.doubleValue()));
        return slider;
    }

    private static CheckBox muteCheckBox(SettingsListener listener) {
        CheckBox mute = new CheckBox(MUTE_LABEL);
        mute.setSelected(listener.isMuted());
        mute.selectedProperty().addListener((property, previous, muted) -> listener.muteToggled(muted));
        return mute;
    }

    public Parent getRoot() {
        return root;
    }
}
