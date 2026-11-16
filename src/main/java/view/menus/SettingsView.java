package view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import service.sound.Volume;
import view.components.container.PopupInstaller;
import view.components.container.TitledPanel;

import java.util.Objects;

/** Volume and mute, in the same blue-and-grey window as the level select. The images live in sokoban.css. */
public class SettingsView {
    private static final double PANEL_WIDTH = 490;
    private static final double LABEL_SPACING = 8;
    private static final double SECTION_SPACING = 28;
    private static final Insets BODY_PADDING = new Insets(28, 8, 12, 8);

    private static final String TITLE = "AJUSTES";
    private static final String VOLUME_LABEL = "Volumen";
    private static final String MUTE_LABEL = "Silenciar juego";
    private static final String ROOT_STYLE = "settings";
    private static final String BACKGROUND_STYLE = "background";
    private static final String TITLE_STYLE = "settings-title";
    private static final String LABEL_STYLE = "settings-label";
    private static final String SLIDER_STYLE = "settings-slider";
    private static final String CHECK_STYLE = "settings-check";

    private final StackPane root = new StackPane();

    public SettingsView(SettingsListener listener, Runnable onClose, PopupInstaller popups) {
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(onClose, "onClose");
        Objects.requireNonNull(popups, "popups");

        StackPane panel = TitledPanel.panel(TitledPanel.card(TitledPanel.titleBar(label(TITLE, TITLE_STYLE)), body(listener)));
        panel.setPrefWidth(PANEL_WIDTH);

        root.getStyleClass().addAll(BACKGROUND_STYLE, ROOT_STYLE);
        popups.install(root, new StackPane(panel), onClose);
    }

    private static VBox body(SettingsListener listener) {
        VBox volume = new VBox(LABEL_SPACING, label(VOLUME_LABEL, LABEL_STYLE), volumeSlider(listener));
        VBox body = new VBox(SECTION_SPACING, volume, muteRow(listener));
        body.setPadding(BODY_PADDING);
        return body;
    }

    private static Slider volumeSlider(SettingsListener listener) {
        Slider slider = new Slider(Volume.MIN, Volume.MAX, listener.currentVolume());
        slider.getStyleClass().add(SLIDER_STYLE);
        slider.setMaxWidth(Double.MAX_VALUE);
        slider.valueProperty().addListener((property, previous, level) -> listener.volumeChanged(level.doubleValue()));
        return slider;
    }

    /** Text on the left, box on the right: the CheckBox has no text of its own. */
    private static HBox muteRow(SettingsListener listener) {
        CheckBox mute = new CheckBox();
        mute.getStyleClass().add(CHECK_STYLE);
        mute.setSelected(listener.isMuted());
        mute.selectedProperty().addListener((property, previous, muted) -> listener.muteToggled(muted));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(label(MUTE_LABEL, LABEL_STYLE), gap, mute);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static Label label(String text, String style) {
        Label label = new Label(text);
        label.getStyleClass().add(style);
        return label;
    }

    public Parent getRoot() {
        return root;
    }
}