package se.gritacademy.tetris;

import java.util.Random;

public class TetrisGame {

    private final GameBoard board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private final GameStats stats;
    private final Random random = new Random();
    private boolean gameOver = false;


    public TetrisGame(long baseDropIntervalNs,
                      long minDropIntervalNs,
                      long dropDecreasePerLevelNs) {
        this.board = new GameBoard();
        this.stats = new GameStats(baseDropIntervalNs, minDropIntervalNs, dropDecreasePerLevelNs);
        this.currentPiece = createRandomTetromino();
        this.nextPiece = createRandomTetromino();
    }


    public void moveDown() {
        if (gameOver) return;

        int[] xs = currentPiece.getX();
        int[] ys = currentPiece.getY();

        for (int i = 0; i < 4; i++) {
            int newRow = ys[i] + 1;

            if (newRow >= GameBoard.ROWS) {
                lockAndSpawnNew();
                return;
            }

            if (board.getCell(newRow, xs[i]) != 0) {
                lockAndSpawnNew();
                return;
            }
        }

        for (int i = 0; i < 4; i++) {
            ys[i]++;
        }
    }

    public void moveHorizontal(int dx) {
        if (gameOver) return;

        int[] xs = currentPiece.getX();
        int[] ys = currentPiece.getY();

        for (int i = 0; i < 4; i++) {
            int newCol = xs[i] + dx;
            int row = ys[i];

            if (newCol < 0 || newCol >= GameBoard.COLUMNS) {
                return;
            }

            if (board.getCell(row, newCol) != 0) {
                return;
            }
        }

        for (int i = 0; i < 4; i++) {
            xs[i] += dx;
        }
    }

    public void hardDrop() {
        if (gameOver) return;

        int[] xs = currentPiece.getX();
        int[] ys = currentPiece.getY();

        while (true) {
            boolean canMove = true;

            for(int i = 0; i < 4; i++) {
                int newRow = ys[i] + 1;

                if (newRow >= GameBoard.ROWS) {
                    canMove = false;
                    break;
                }

                if (board.getCell(newRow, xs[i]) != 0) {
                    canMove = false;
                    break;
                }
            }

            if (!canMove) {
                lockAndSpawnNew();
                return;
            }

            for (int i = 0; i < 4; i++) {
                ys[i]++;
            }
        }
    }

    public void rotate() {
        if (gameOver) return;

        TetrominoType type = currentPiece.getType();
        if (type == TetrominoType.O) {
            return;
        }

        int[] xs = currentPiece.getX();
        int[] ys = currentPiece.getY();

        int pivotX = xs[1];
        int pivotY = ys[1];

        int[] newX = new int[4];
        int[] newY = new int[4];

        for (int i = 0; i < 4; i++) {
            int dx = xs[i] - pivotX;
            int dy = ys[i] - pivotY;

            int rotatedDx = -dy;
            int rotatedDy = dx;

            newX[i] = pivotX + rotatedDx;
            newY[i] = pivotY + rotatedDy;
        }

        for (int i = 0; i < 4; i++) {
            int col = newX[i];
            int row = newY[i];

            if (col < 0 || col >= GameBoard.COLUMNS) return;
            if (row < 0 || row >= GameBoard.ROWS) return;

            if (board.getCell(row, col) != 0) return;
        }

        for (int i = 0; i < 4; i++) {
            xs[i] = newX[i];
            ys[i] = newY[i];
        }
    }

    public void reset() {
        for (int r = 0; r < GameBoard.ROWS; r++) {
            for (int c = 0; c < GameBoard.COLUMNS; c++) {
                board.setCell(r, c, 0);
            }
        }

        stats.reset();
        currentPiece = createRandomTetromino();
        nextPiece = createRandomTetromino();
        gameOver = false;
    }


    public GameBoard getBoard() {
        return board;
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public Tetromino getNextPiece() {
        return nextPiece;
    }

    public GameStats getStats() {
        return stats;
    }

    public boolean isGameOver() {
        return gameOver;
    }


    private Tetromino createRandomTetromino() {
        TetrominoType[] types = TetrominoType.values();
        int index = random.nextInt(types.length);
        TetrominoType randomType = types[index];
        return new Tetromino(randomType);
    }

    private void lockAndSpawnNew() {
        lockPiece();

        int clearedLines = board.clearFullLines();
        if(clearedLines > 0) {
            stats.addClearedLines(clearedLines);
        }

        currentPiece = nextPiece;
        nextPiece = createRandomTetromino();

        if (collidesImmediately(currentPiece)) {
            gameOver = true;
        }
    }

    private void lockPiece() {
        int[] xs = currentPiece.getX();
        int[] ys = currentPiece.getY();
        int colorCode = currentPiece.getColorCode();

        for (int i = 0; i < 4; i++) {
            int col = xs[i];
            int row = ys[i];
            board.setCell(row, col, colorCode);
        }
    }

    private boolean collidesImmediately(Tetromino piece) {
        int[] xs = piece.getX();
        int[] ys = piece.getY();

        for (int i = 0; i < 4; i++) {
            int col = xs[i];
            int row = ys[i];

            if (row < 0 || row >= GameBoard.ROWS || col < 0 || col >= GameBoard.COLUMNS) {
                continue;
            }

            if (board.getCell(row, col) != 0) {
                return true;
            }
        }

        return false;
    }
}
