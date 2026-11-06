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

    public BoardView(SpriteCatalog sprites) {
        this.sprites = Objects.requireNonNull(sprites, "sprites");
        setMinSize(0, 0);                     // que pueda achicarse dentro de un BorderPane
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

        Camera.Offset camera = Camera.follow(
                (player.col(now) + 0.5) * TILE, (player.row(now) + 0.5) * TILE,
                viewW, viewH, board.getCols() * TILE, board.getRows() * TILE);
        double offX = Math.round(camera.x());          // enteros: sin costuras entre casilleros
        double offY = Math.round(camera.y());

        int firstRow = Math.max(0, (int) Math.floor(offY / TILE));
        int lastRow = Math.min(board.getRows() - 1, (int) Math.floor((offY + viewH) / TILE));
        int firstCol = Math.max(0, (int) Math.floor(offX / TILE));
        int lastCol = Math.min(board.getCols() - 1, (int) Math.floor((offX + viewW) / TILE));

        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                drawCell(g, board.cellAt(new Position(row, col)), visibility,
                        col * TILE - offX, row * TILE - offY);
            }
        }
        g.drawImage(sprites.get(player.sprite(now)),
                player.col(now) * TILE - offX, player.row(now) * TILE - offY, TILE, TILE);
    }

    private void drawCell(GraphicsContext g, Cell cell, Visibility visibility, double x, double y) {
        switch (CellSprites.appearanceOf(cell, visibility)) {
            case OUTSIDE -> { }                                  // se ve el pasto del fondo
            case HIDDEN -> {
                g.setFill(UNSEEN);
                g.fillRect(x, y, TILE, TILE);
            }
            case VISIBLE -> CellSprites.layers(cell)
                    .forEach(key -> g.drawImage(sprites.get(key), x, y, TILE, TILE));
        }
    }
}