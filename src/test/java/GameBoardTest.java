import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.gritacademy.tetris.GameBoard;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameBoardTest {

    @Test
    @DisplayName("clearFullLines ska inte rensa något när ingen rad är full")
    void clearFullLinesShouldReturnZeroWhenNoFullLines() {
        GameBoard board = new GameBoard();

        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLUMNS - 1; col++) {
                board.setCell(row, col, 1);
            }

            board.setCell(row, GameBoard.COLUMNS - 1, 0);
        }

        int result = board.clearFullLines();

        assertEquals(0, result);
    }

    @Test
    @DisplayName("clearFullLines ska rensa en full rad och flytta ner blocken ovanför")
    void clearFullLinesShouldClearFullLineAndShiftBlocksDown() {
        GameBoard board = new GameBoard();

        int bottomRow = GameBoard.ROWS - 1;
        int rowAbove = GameBoard.ROWS - 2;

        for (int col = 0; col < GameBoard.COLUMNS; col++) {
            board.setCell(bottomRow, col, 1);
        }

        board.setCell(rowAbove, 0, 2);

        int result = board.clearFullLines();

        assertEquals(1, result);

        assertEquals(2, board.getCell(bottomRow, 0));

        for(int col = 0; col < GameBoard.COLUMNS; col++) {
            assertEquals(0, board.getCell(0, col));
        }
    }

    @Test
    @DisplayName("clearFullLines ska kunna rensa flera rader")
    void clearFullLinesShouldClearMultipleFullLines() {
        GameBoard board = new GameBoard();

        int bottomRow = GameBoard.ROWS - 1;
        int secondBottomRow = GameBoard.ROWS - 2;
        int rowAbove = GameBoard.ROWS - 3;

        for (int col = 0; col < GameBoard.COLUMNS; col++) {
            board.setCell(bottomRow, col, 1);
            board.setCell(secondBottomRow, col, 1);
        }

        board.setCell(rowAbove, 0, 3);

        int result = board.clearFullLines();

        assertEquals(2, result);

        assertEquals(3, board.getCell(bottomRow, 0));

        for(int col = 0; col < GameBoard.COLUMNS; col++) {
            for(int row = 0; row < GameBoard.ROWS; row++) {
                assertEquals(0, board.getCell(0, col));
            }
        }
    }

    @Test
    @DisplayName("Ett nytt board ska vara helt tomt")
    void newBoardShouldBeCompletelyEmpty() {
        GameBoard board = new GameBoard();
        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLUMNS; col++) {
                assertEquals(0, board.getCell(row, col));
            }
        }
    }

    @Test
    @DisplayName("clearFullLines ska hantera fulla rader med tomma rader emellan")
    void clearFullLinesShouldHandleGapsBetweenFullLines() {
        GameBoard board = new GameBoard();
        int bottom = GameBoard.ROWS - 1; // Rad 19
        int middle = GameBoard.ROWS - 2; // Rad 18 som ska vara tom
        int aboveMiddle = GameBoard.ROWS - 3;// Rad 17

        // Fyller rad 19 + 17. Lämnar 18 tom
        for (int col = 0; col < GameBoard.COLUMNS; col++) {
            board.setCell(bottom, col, 1);
            board.setCell(aboveMiddle, col, 2);
        }

        // lägger en kontroll-bit på den tomma raden, 18
        board.setCell(middle, 5, 5);

        int result = board.clearFullLines();

        assertEquals(2, result, "Två rader ska ha rensats");

        // Kontrollbiten från rad 18 ska nu ligga på 19
        assertEquals(5, board.getCell(bottom, 5), "Bitarna ska ha flyttats ner");
        // Rad 18 ska nu vara tom
        assertEquals(0, board.getCell(middle, 5));
    }

    @Test
    @DisplayName("setCell och getCell ska fungera på brädets ytterkanter")
    void setAndGetCellShouldWorkAtBoundaries() {
        GameBoard board = new GameBoard();

        // Översta vänster hörn
        board.setCell(0, 0, 7);
        // Nedersta höger hörn
        board.setCell(GameBoard.ROWS -1, GameBoard.COLUMNS - 1, 4);

        assertEquals(7, board.getCell(0, 0));
        assertEquals(4, board.getCell(GameBoard.ROWS - 1, GameBoard.COLUMNS - 1));
    }
}
