package view.menus;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import view.components.images.ImageProvider;
import view.components.images.UiImage;

import java.util.Objects;

/** Pantalla de inicio: muestra el logo del grupo mientras crece levemente y luego avisa que terminó. */
public class SplashView {
    private static final Duration DURATION = Duration.seconds(3);
    private static final double GROWTH = 1.05;
    private static final double NO_MIN_SIZE = 0;
    private static final String ROOT_STYLE = "splash";

    private final StackPane root = new StackPane();
    private final ScaleTransition grow;
    private final Runnable onStart;

    public SplashView(ImageProvider images, Runnable onStart, Runnable onFinished) {
        Objects.requireNonNull(images, "images");
        this.onStart = Objects.requireNonNull(onStart, "onStart");
        Objects.requireNonNull(onFinished, "onFinished");

        ImageView logo = new ImageView(images.get(UiImage.SPLASH_LOGO));
        logo.setPreserveRatio(true);
        logo.fitWidthProperty().bind(root.widthProperty());
        logo.fitHeightProperty().bind(root.heightProperty());

        root.setMinSize(NO_MIN_SIZE, NO_MIN_SIZE);
        root.getStyleClass().add(ROOT_STYLE);
        root.getChildren().add(logo);

        grow = new ScaleTransition(DURATION, logo);
        grow.setToX(GROWTH);
        grow.setToY(GROWTH);
        grow.setInterpolator(Interpolator.EASE_OUT);
        grow.setOnFinished(e -> onFinished.run());
    }

    /** Arranca la animación y dispara el aviso de inicio (la música empieza acá). */
    public void play() {
        onStart.run();
        grow.playFromStart();
    }

    public Parent getRoot() { return root; }
}