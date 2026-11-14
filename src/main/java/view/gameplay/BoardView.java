package view.gameplay;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import model.board.Board;
import model.board.Cell;
import model.board.Position;
import model.board.Visibility;
import model.game.Level;
import view.SpriteCatalog;

import java.util.Objects;


public class BoardView extends Region {

    static final double TILE = 64;
    /** Si el mapa no entra en pantalla los casilleros se achican, pero nunca por debajo de esto (ahí scrollea la cámara). */
    static final double MIN_TILE = 24;
    private static final Color UNSEEN = Color.BLACK;

    private final Canvas canvas = new Canvas();
    private final SpriteCatalog sprites;
    private final PlayerAnimator player = new PlayerAnimator();
    private final AnimationTimer loop = new AnimationTimer() {
        @Override
        public void handle(long ignored) {
            long now = System.nanoTime();
            draw(now);
            if (!player.isMoving(now)) {
                stop();
            }
        }
    };
    private Level level;
    private double topInset;

    public BoardView(SpriteCatalog sprites) {
        this.sprites = Objects.requireNonNull(sprites, "sprites");
        setMinSize(0, 0);                     // que pueda achicarse con la ventana
        getChildren().add(canvas);
    }


    public void setLevel(Level level) {
        this.level = Objects.requireNonNull(level, "level");
        loop.stop();
        Board board = level.getBoard();
        player.snapTo(board.getPlayerPosition(), board.getPlayer().getFacing());
        draw(System.nanoTime());
    }


    public void onPlayerMoved() {
        player.moveTo(level.getBoard().getPlayerPosition(), System.nanoTime());
        loop.start();                         // si ya corría, no hace nada
    }

    public void refresh() {
        draw(System.nanoTime());
    }

    /** Alto (px) de la franja de arriba que tapa el HUD: la cámara no deja el mapa debajo de ella. */
    public void setTopInset(double topInset) {
        if (topInset != this.topInset) {
            this.topInset = topInset;
            draw(System.nanoTime());
        }
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        draw(System.nanoTime());
    }

    private void draw(long now) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double viewW = canvas.getWidth();
        double viewH = canvas.getHeight();
        g.clearRect(0, 0, viewW, viewH);
        if (level == null) {
            return;
        }
        Board board = level.getBoard();
        Visibility visibility = level.getVisibility();

        double tile = tileSize(board, viewW, viewH);
        Camera.Offset camera = Camera.follow(
                (player.col(now) + 0.5) * tile, (player.row(now) + 0.5) * tile,
                viewW, viewH, board.getCols() * tile, board.getRows() * tile, topInset);

        double offX = Math.round(camera.x());          // enteros: sin costuras entre casilleros
        double offY = Math.round(camera.y());

        int firstRow = Math.max(0, (int) Math.floor(offY / tile));
        int lastRow = Math.min(board.getRows() - 1, (int) Math.floor((offY + viewH) / tile));
        int firstCol = Math.max(0, (int) Math.floor(offX / tile));
        int lastCol = Math.min(board.getCols() - 1, (int) Math.floor((offX + viewW) / tile));

        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                drawCell(g, board.cellAt(new Position(row, col)), visibility,
                        col * tile - offX, row * tile - offY, tile);
            }
        }
        g.drawImage(sprites.get(player.sprite(now)),
                player.col(now) * tile - offX, player.row(now) * tile - offY, tile, tile);
    }

    /** Lado del casillero: 64px si el mapa entra; si no, lo justo para que entre entero debajo del HUD. */
    private double tileSize(Board board, double viewW, double viewH) {
        double fit = Math.min(viewW / board.getCols(), (viewH - topInset) / board.getRows());
        return Math.clamp(Math.floor(fit), MIN_TILE, TILE);   // entero: sin costuras entre casilleros
    }

    private void drawCell(GraphicsContext g, Cell cell, Visibility visibility, double x, double y, double tile) {
        switch (CellSprites.appearanceOf(cell, visibility)) {
            case OUTSIDE -> { }                                  // se ve el pasto del fondo
            case HIDDEN -> {
                g.setFill(UNSEEN);
                g.fillRect(x, y, tile, tile);
            }
            case VISIBLE -> CellSprites.layers(cell)
                    .forEach(key -> g.drawImage(sprites.get(key), x, y, tile, tile));
        }
    }
}