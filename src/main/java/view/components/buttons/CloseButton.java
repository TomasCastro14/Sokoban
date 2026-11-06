package view.components.buttons;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import view.components.images.UiImage;
import view.components.images.ImageProvider;

class CloseButton extends Button {
    private static final String STYLE_CLASS = "close-button";
    private static final double WIDTH = 56;
    private static final double CROSS_WIDTH_RATIO = 0.30;
    private static final double CROSS_CENTER_RATIO = 0.36;

    CloseButton(ImageProvider images) {
        Image round = images.get(UiImage.CLOSE_ROUND);
        Image square = images.get(UiImage.CLOSE_SQUARE);

        double topHalfHeight = round.getHeight() / 2;
        double bodyHeight = square.getHeight() * 2 / 3;
        ImageView top = slice(round, 0, topHalfHeight);
        ImageView body = slice(square, square.getHeight() / 3, bodyHeight);
        body.setLayoutY(scaledHeight(round, topHalfHeight));

        double artHeight = scaledHeight(round, topHalfHeight) + scaledHeight(square, bodyHeight);
        Pane art = new Pane(body, top, cross(images.get(UiImage.CLOSE_CROSS)));
        art.setPrefSize(WIDTH, artHeight);
        art.setMinSize(WIDTH, artHeight);
        art.setMaxSize(WIDTH, artHeight);

        setGraphic(art);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        getStyleClass().add(STYLE_CLASS);
        setFocusTraversable(false);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    private static ImageView slice(Image image, double fromY, double height) {
        ImageView view = new ImageView(image);
        view.setViewport(new Rectangle2D(0, fromY, image.getWidth(), height));
        view.setFitWidth(WIDTH);
        view.setPreserveRatio(true);
        return view;
    }

    private static double scaledHeight(Image image, double sliceHeight) {
        return WIDTH * sliceHeight / image.getWidth();
    }

    private static ImageView cross(Image image) {
        double width = WIDTH * CROSS_WIDTH_RATIO;
        double height = width * image.getHeight() / image.getWidth();
        ImageView view = new ImageView(image);
        view.setFitWidth(width);
        view.setPreserveRatio(true);
        view.setLayoutX((WIDTH - width) / 2);
        view.setLayoutY(WIDTH * CROSS_CENTER_RATIO - height / 2);
        return view;
    }
}