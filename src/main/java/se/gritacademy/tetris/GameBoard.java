package se.gritacademy.tetris;

public class GameBoard {

    // Storlek på ruta/kolumn/rad i pixlar
    public static final int TILE_SIZE = 25;
    public static final int COLUMNS = 10;
    public static final int ROWS = 20;

    // Spelplan i 2D. 0 = tom, >0 fylld ruta
    private final int [][] grid = new int[ROWS][COLUMNS];

    public GameBoard() {}

    public int getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, int value) {
        grid[row][col] = value;
    }

    public int[][] getGrid() {
        return grid;
    }

    // Rensar fulla rader och droppar raderna ovanför
    // Returnerar antal rensade rader
    public int clearFullLines() {
        int linesCleared = 0;

        // Går igenom rader nedifrån
        for (int row = ROWS - 1; row >= 0; row--) {
            boolean full = true;

            // Går igenom columner från vänster
            for (int col = 0; col < COLUMNS; col++) {

                // Kollar om ruta är tom. Då bryts inre loop och börjar på nästa rad ovanför
                if (grid[row][col] == 0) {
                    full = false;
                    break;
                }
            }

            // Om ingen tom ruta hittas tas raden bort och allt ovanför flyttas ned.
            if (full) {
                // Varje rad r kopieras från raden ovanför r-1
                for (int r = row; r > 0; r--) {
                    for (int c = 0; c < COLUMNS; c++) {
                        grid[r][c] = grid[r - 1][c];
                    }
                }

                // Tömmer översta raden
                for (int c = 0; c < COLUMNS; c++) {
                    grid[0][c] = 0;
                }

                // Ökar antalet rensade rader
                linesCleared++;

                // Då raderna justerats efter radrensning behöver vi kolla samma igen och kör
                // därför ++ för att inte gå upp en rad i loopen.
                row++;
            }
        }

        // Returnerar antal rensade rader. Används till att justera level, score och lines cleared.
        return linesCleared;
    }

}
