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
}
