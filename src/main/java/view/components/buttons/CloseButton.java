package view.components.buttons;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.net.URL;

/** Botón de cierre universal: semicírculo + 2/3 de cuadrado + cruz. */
public class CloseButton extends Button {

    private static final String DIR = "/assets/images/ui/buttons/rounded_close/";
    private static final double WIDTH = 56;        // ancho del botón
    private static final double CROSS_W = 0.30;    // ancho de la cruz respecto de WIDTH
    private static final double CROSS_Y = 0.36;    // altura del centro de la cruz respecto de WIDTH
    private static final double PEEK = 44;         // cuánto sobresale por arriba del panel
    private static final double RIGHT_INSET = 32;  // distancia al borde derecho del panel

    public CloseButton(Runnable onClose) {
        Image round = image("button_round_border.png");
        Image square = image("button_square_border.png");
        Image cross = image("icon_cross.png");

        // mitad superior del círculo
        ImageView top = new ImageView(round);
        top.setViewport(new Rectangle2D(0, 0, round.getWidth(), round.getHeight() / 2));
        top.setFitWidth(WIDTH);
        top.setPreserveRatio(true);
        double topH = WIDTH * (round.getHeight() / 2) / round.getWidth();

        // 2/3 inferiores del cuadrado (el corte cae donde solo hay lados rectos)
        ImageView body = new ImageView(square);
        body.setViewport(new Rectangle2D(0, square.getHeight() / 3, square.getWidth(), square.getHeight() * 2 / 3));
        body.setFitWidth(WIDTH);
        body.setPreserveRatio(true);
        body.setLayoutY(topH);
        double bodyH = WIDTH * (square.getHeight() * 2 / 3) / square.getWidth();

        // cruz sobre el semicírculo
        ImageView x = new ImageView(cross);
        double cw = WIDTH * CROSS_W;
        double ch = cw * cross.getHeight() / cross.getWidth();
        x.setFitWidth(cw);
        x.setPreserveRatio(true);
        x.setLayoutX((WIDTH - cw) / 2);
        x.setLayoutY(WIDTH * CROSS_Y - ch / 2);

        Pane art = new Pane(body, top, x);
        art.setPrefSize(WIDTH, topH + bodyH);
        art.setMinSize(WIDTH, topH + bodyH);
        art.setMaxSize(WIDTH, topH + bodyH);

        setGraphic(art);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        getStyleClass().add("close-button");
        setFocusTraversable(false);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setOnAction(e -> onClose.run());
    }

    /** Lo coloca detrás de todo lo que ya tenga el contenedor, arriba a la derecha del panel. */
    public static CloseButton install(StackPane container, Runnable onClose) {
        CloseButton b = new CloseButton(onClose);
        StackPane.setAlignment(b, Pos.TOP_RIGHT);
        StackPane.setMargin(b, new Insets(-PEEK, RIGHT_INSET, 0, 0));
        container.getChildren().add(0, b);       // índice 0 = detrás del panel
        return b;
    }

    private static Image image(String name) {
        URL url = CloseButton.class.getResource(DIR + name);
        if (url == null) throw new IllegalStateException("Falta asset: " + DIR + name);
        return new Image(url.toExternalForm());
    }
}