package view.gameplay;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import model.board.Board;
import model.board.Cell;
import model.board.Position;
import model.board.Visibility;
import model.game.Level;
import view.SpriteCatalog;

import java.util.Objects;


public class BoardView extends Region {

    static final double TILE = 64;

    private final Canvas canvas = new Canvas();
    private final SpriteCatalog sprites;
    private final PlayerAnimator player = new PlayerAnimator();
    private final WearBubbles wearBubbles;
    private final FogLayer fog = new FogLayer(TILE);
    private final AnimationTimer loop = new AnimationTimer() {
        @Override
        public void handle(long ignored) {
            long now = System.nanoTime();
            draw(now);
            if (!player.isMoving(now) && !fog.isFading(now)) {
                stop();
            }
        }
    };
    private Level level;
    private double topInset;

    public BoardView(SpriteCatalog sprites) {
        this.sprites = Objects.requireNonNull(sprites, "sprites");
        this.wearBubbles = new WearBubbles(sprites, TILE);
        setMinSize(0, 0);                     // que pueda achicarse con la ventana
        getChildren().add(canvas);
    }


    public void setLevel(Level level) {
        this.level = Objects.requireNonNull(level, "level");
        loop.stop();
        Board board = level.getBoard();
        player.snapTo(board.getPlayerPosition(), board.getPlayer().getFacing());
        fog.reset(board, level.getVisibility());
        draw(System.nanoTime());
    }


    public void onPlayerMoved() {
        player.moveTo(level.getBoard().getPlayerPosition(), System.nanoTime());
        loop.start();                         // si ya corría, no hace nada
    }

    public void refresh() {
        if (level == null) {
            return;
        }
        long now = System.nanoTime();
        fog.update(level.getBoard(), level.getVisibility(), now);
        draw(now);
        if (fog.isFading(now)) {
            loop.start();                     // anima la nube que se disipa
        }
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

        Camera.Offset camera = Camera.follow(
                (player.col(now) + 0.5) * TILE, (player.row(now) + 0.5) * TILE,
                viewW, viewH, board.getCols() * TILE, board.getRows() * TILE, topInset);

        double offX = Math.round(camera.x());          // enteros: sin costuras entre casilleros
        double offY = Math.round(camera.y());

        TileRange onScreen = new TileRange(
                Math.max(0, (int) Math.floor(offY / TILE)),
                Math.min(board.getRows() - 1, (int) Math.floor((offY + viewH) / TILE)),
                Math.max(0, (int) Math.floor(offX / TILE)),
                Math.min(board.getCols() - 1, (int) Math.floor((offX + viewW) / TILE)));

        // Orden de capas: piso visible, nube (asoma sobre el borde de lo visible), paredes, jugador.
        drawCells(g, board, visibility, onScreen, CellSprites.Appearance.VISIBLE, offX, offY);
        fog.draw(g, onScreen.grow(1), offX, offY, now);  // +1: las nubes de afuera de la pantalla asoman adentro
        drawCells(g, board, visibility, onScreen, CellSprites.Appearance.ABOVE_FOG, offX, offY);
        g.drawImage(sprites.get(player.sprite(now)),
                player.col(now) * TILE - offX, player.row(now) * TILE - offY, TILE, TILE);
        wearBubbles.draw(g, board, visibility, offX, offY);
    }

    private void drawCells(GraphicsContext g, Board board, Visibility visibility, TileRange range,
                           CellSprites.Appearance appearance, double offX, double offY) {
        range.forEach(position -> {
            Cell cell = board.cellAt(position);
            if (CellSprites.appearanceOf(cell, visibility) == appearance) {
                double x = position.col() * TILE - offX;
                double y = position.row() * TILE - offY;
                CellSprites.layers(cell).forEach(key -> g.drawImage(sprites.get(key), x, y, TILE, TILE));
            }
        });
    }
}
