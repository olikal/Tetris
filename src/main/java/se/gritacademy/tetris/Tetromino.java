package se.gritacademy.tetris;

public class Tetromino {

    private int[] x = new int[4];
    private int[] y = new int[4];

    private int colorCode;
    private TetrominoType type;

    public Tetromino() {
        this(TetrominoType.I);
    }

    public Tetromino(TetrominoType type) {
        this.type = type;

        switch (type) {
            case I -> {
                x[0] = 3; y[0] = 0;
                x[1] = 4; y[1] = 0;
                x[2] = 5; y[2] = 0;
                x[3] = 6; y[3] = 0;

                colorCode = 1;
            }
            case O -> {
                x[0] = 4; y[0] = 0;
                x[1] = 5; y[1] = 0;
                x[2] = 4; y[2] = 1;
                x[3] = 5; y[3] = 1;

                colorCode = 2;
            }
            case T -> {
                x[0] = 3; y[0] = 1;
                x[1] = 4; y[1] = 1;
                x[2] = 5; y[2] = 1;
                x[3] = 4; y[3] = 0;

                colorCode = 3;
            }
            case L -> {
                x[0] = 4; y[0] = 0;
                x[1] = 4; y[1] = 1;
                x[2] = 4; y[2] = 2;
                x[3] = 5; y[3] = 2;

                colorCode = 4;
            }
            case J -> {
                x[0] = 4; y[0] = 0;
                x[1] = 4; y[1] = 1;
                x[2] = 4; y[2] = 2;
                x[3] = 3; y[3] = 2;

                colorCode = 5;
            }
            case S -> {
                x[0] = 3; y[0] = 1;
                x[1] = 4; y[1] = 1;
                x[2] = 4; y[2] = 0;
                x[3] = 5; y[3] = 0;

                colorCode = 6;
            }
            case Z -> {
                x[0] = 3; y[0] = 0;
                x[1] = 4; y[1] = 1;
                x[2] = 5; y[2] = 1;
                x[3] = 4; y[3] = 0;

                colorCode = 7;
            }
        }
    }

    public int[] getX() {
        return x;
    }

    public int[] getY() {
        return y;
    }

    public int getColorCode() {
        return colorCode;
    }

    public TetrominoType getType() {
        return type;
    }
}
