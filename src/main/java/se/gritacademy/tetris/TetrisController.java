package se.gritacademy.tetris;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TetrisController {

    @FXML
    private Pane gameArea;
    @FXML
    private Label linesLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Pane nextArea;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label hs1, hs2, hs3, hs4, hs5;

    private final List<Integer> highscores = new ArrayList<>();

    private Canvas canvas;
    private GameBoard gameBoard;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Canvas nextCanvas;
    private GameStats stats;
    private TetrisGame game;

    private AnimationTimer gameLoop;
    private long lastUpdate = 0;

    private static final long BASE_DROP_INTERVAL_NS = 500_000_000L;
    private static final long MIN_DROP_INTERVAL_NS = 100_000_000L;
    private static final long DROP_DECREASE_PER_LEVEL_NS = 50_000_000L;


    @FXML
    public void initialize() {
        game = new TetrisGame(
                BASE_DROP_INTERVAL_NS,
                MIN_DROP_INTERVAL_NS,
                DROP_DECREASE_PER_LEVEL_NS
        );

        gameBoard = game.getBoard();
        currentPiece = game.getCurrentPiece();
        nextPiece = game.getNextPiece();
        stats = game.getStats();

        canvas = new Canvas(
            GameBoard.COLUMNS * GameBoard.TILE_SIZE,
            GameBoard.ROWS * GameBoard.TILE_SIZE
        );
        gameArea.getChildren().add(canvas);

        nextCanvas = new Canvas(4 * GameBoard.TILE_SIZE, 4 * GameBoard.TILE_SIZE);
        nextArea.getChildren().add(nextCanvas);

        if (linesLabel != null) linesLabel.setText("Lines: " + stats.getTotalLinesCleared());
        if (levelLabel != null) levelLabel.setText("Level: " + stats.getLevel());

        scoreLabel.setText("Score: " + stats.getScore());
        refreshHighscoreLabels();

        renderNextPiece();
        startGameLoop();
    }

    public void initInput(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> moveCurrentPieceHorizontal(-1);
                case RIGHT -> moveCurrentPieceHorizontal(1);
                case UP -> rotateCurrentPiece();
                case DOWN -> {
                    moveCurrentPieceDown();
                    render();
                }
                case SPACE -> hardDropCurrentPiece();
            }
        });
    }

    private void startGameLoop() {
        render();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                if (now - lastUpdate > stats.getCurrentDropIntervalNs()) {
                    moveCurrentPieceDown();
                    render();
                    lastUpdate = now;
                }
            }
        };

        gameLoop.start();
    }

    private void moveCurrentPieceDown() {
        game.moveDown();

        currentPiece = game.getCurrentPiece();
        nextPiece = game.getNextPiece();

        updateStatsUI();
        renderNextPiece();

        if (game.isGameOver()) {
            gameLoop.stop();
            updateHighscores(stats.getScore());
            showGameOverDialog();
        }
    }

    private void moveCurrentPieceHorizontal(int dx) {
        game.moveHorizontal(dx);

        currentPiece = game.getCurrentPiece();
        nextPiece = game.getNextPiece();

        render();
    }

    private void hardDropCurrentPiece() {
        game.hardDrop();

        currentPiece = game.getCurrentPiece();
        nextPiece = game.getNextPiece();

        updateStatsUI();
        renderNextPiece();

        if(game.isGameOver()) {
            gameLoop.stop();
            updateHighscores(stats.getScore());
            showGameOverDialog();
            return;
        }

        render();
    }

    private void rotateCurrentPiece() {
        game.rotate();

        currentPiece = game.getCurrentPiece();

        render();
    }

    private void restartGame() {
        game.reset();

        currentPiece = game.getCurrentPiece();
        nextPiece = game.getNextPiece();

        updateStatsUI();
        renderNextPiece();

        lastUpdate = 0;
        gameLoop.start();
    }

    private void render() {
        var gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.web("#222222"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(0.5);

        for (int col = 0; col <= GameBoard.COLUMNS; col++) {
            double x = col * GameBoard.TILE_SIZE + 0.5;
            gc.strokeLine(x, 0, x, GameBoard.ROWS * GameBoard.TILE_SIZE);
        }

        for (int row = 0; row <= GameBoard.ROWS; row++) {
            double y = row * GameBoard.TILE_SIZE + 0.5;
            gc.strokeLine(0, y, GameBoard.COLUMNS * GameBoard.TILE_SIZE, y);
        }

        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLUMNS; col++) {
                int cellValue = gameBoard.getCell(row, col);

                if (cellValue != 0) {
                    drawBlock(gc, col, row, colorForCode(cellValue));
                }
            }
        }

        int[] xs = currentPiece.getX();
        int[] ys = currentPiece.getY();
        Color currentColor = colorForCode(currentPiece.getColorCode());

        for (int i = 0; i < 4; i++) {
            int col = xs[i];
            int row = ys[i];
            drawBlock(gc, col, row, currentColor);
        }
    }

    private void renderNextPiece() {
        if (nextCanvas == null || nextPiece == null) return;

        GraphicsContext gc = nextCanvas.getGraphicsContext2D();

        gc.setFill(Color.web("#111111"));
        gc.fillRect(0, 0, nextCanvas.getWidth(), nextCanvas.getHeight());

        int[] xs = nextPiece.getX();
        int[] ys = nextPiece.getY();
        Color color = colorForCode(nextPiece.getColorCode());

        int minX = xs[0];
        int minY = ys[0];
        for(int i = 1; i < 4; i++) {
            if (xs[i] < minX) minX = xs[i];
            if (ys[i] < minY) minY = ys[i];
        }

        for(int i = 0; i < 4; i++) {
            int localCol = xs[i] - minX;
            int localRow = ys[i] - minY;

            drawBlock(gc, localCol, localRow, color);
        }
    }

    private void drawBlock(GraphicsContext gc, int col, int row, Color baseColor) {
        double x = col * GameBoard.TILE_SIZE;
        double y = row * GameBoard.TILE_SIZE;
        double size = GameBoard.TILE_SIZE;

        double padding = 0.75;
        x += padding;
        y += padding;
        size -= padding * 2;

        gc.setFill(baseColor);
        gc.fillRect(x, y, size, size);
    }

    private Color colorForCode(int code) {
        return switch (code) {
            case 1 -> Color.web("#53BDF2");
            case 2 -> Color.web("#F5D76E");
            case 3 -> Color.web("#D66AE5");
            case 4 -> Color.web("#F4A45B");
            case 5 -> Color.web("#4F76E8");
            case 6 -> Color.web("#6DDB7B");
            case 7 -> Color.web("#F06868");
            default -> Color.GRAY;
        };
    }

    private void updateStatsUI() {
        scoreLabel.setText("Score: " + stats.getScore());

        if(linesLabel != null) linesLabel.setText("Lines: " + stats.getTotalLinesCleared());
        if(levelLabel != null) levelLabel.setText("Level: " + stats.getLevel());
    }

    private void showGameOverDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Game Over");

        Label title = new Label("GAME OVER");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label scoreText = new Label("Your score: " + stats.getScore());

        Button restartButton = new Button("Restart");
        Button quitButton = new Button("Quit");

        restartButton.setOnAction(e -> {
            dialog.close();
            restartGame();
        });

        quitButton.setOnAction(e -> {
            dialog.close();
            Platform.exit();
        });

        HBox buttons = new HBox(10, restartButton, quitButton);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, title, scoreText, buttons);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(layout, 250,200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void updateHighscores(int newScore) {
        highscores.add(newScore);
        highscores.sort((a, b) -> b - a);

        if (highscores.size() > 5) {
            highscores.remove(5);
        }

        refreshHighscoreLabels();
    }

    private void refreshHighscoreLabels() {
        if (hs1 == null) return;

        Label[] labels = { hs1, hs2, hs3, hs4, hs5 };

        for(int i = 0; i < 5; i++) {
            if (i < highscores.size()) {
                labels[i].setText((i + 1) + ". " + highscores.get(i));
            } else {
                labels[i].setText((i + 1) + ". -");
            }
        }
    }
}
