import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import se.gritacademy.tetris.GameBoard;
import se.gritacademy.tetris.TetrisGame;
import se.gritacademy.tetris.Tetromino;

import static org.junit.jupiter.api.Assertions.*;

public class TetrisGameTest {

    private TetrisGame game;

    private static final long BASE = 500_000_000L;
    private static final long MIN = 100_000_000L;
    private static final long STEP = 50_000_000L;

    @BeforeEach
    void setUp() {
        game = new TetrisGame(BASE, MIN, STEP);
    }

    @Test
    @DisplayName("moveDown ska flytta biten ett steg nedåt")
    void moveDownShouldIncrementY() {
        Tetromino piece = game.getCurrentPiece();
        int[] originalY = piece.getY().clone();

        game.moveDown();

        // Verifierar att bitarna faller nedåt med 1 y-värde för varje block
        int[] newY = piece.getY();
        for (int i = 0; i < 4; i++) {
            assertEquals(originalY[i] + 1, newY[i], "Varje block i biten ska ha flyttats ner ett steg");
        }
    }

    @Test
    @DisplayName("hardDrop ska låsa biten ch generera ny bit")
    void hardDropShouldLockPieceAndSpawnNewPiece() {
        Tetromino firstPiece = game.getCurrentPiece();

        // Droppar biten direkt
        game.hardDrop();

        Tetromino secondPiece = game.getCurrentPiece();

        // Kontrollerar att livscykeln fungerar. När en bit landar ska en ny skapas vid toppen
        assertNotSame(firstPiece, secondPiece, "En ny Tetromino borde ha skapats");
        assertTrue(secondPiece.getY()[0] <= 1, "Den nya biten ska starta vid toppen");
    }

    @Test
    @DisplayName("Biten ska inte kunna flyttas utanför brädets sidokanter")
    void moveHorizontalShouldRespectBoundaries() {
        // Försöker flytta biten långt utanför vänster vägg
        for (int i = 0; i < 10; i++) {
            game.moveHorizontal(-1);
        }

        for (int x : game.getCurrentPiece().getX()) {
            assertTrue(x >= 0, "X-koordinaten får inte vara negativ");
        }

        // Försöker flytta biten långt utanför höger vägg
        for (int i = 0; i < 10; i++) {
            game.moveHorizontal(1);
        }

        for (int x : game.getCurrentPiece().getX()) {
            assertTrue(x < GameBoard.COLUMNS, "X-koordinaten får inte vara större än brädets bredd");
        }
    }

    @Test
    @DisplayName("Game Over ska triggas om brädet är blockat vid start")
    void gameOverShouldTriggerWhenBoardIsFull() {
        GameBoard board = game.getBoard();

        // Blockerar spawn-punkten (rad 0 i mitten)
        board.setCell(0, 4, 1);

        // Triggar ny spawn
        game.hardDrop();

        assertTrue(game.isGameOver(), "isGameOver() ska returnera true när brädet är 'fullt' vid toppen");
    }

    @RepeatedTest(100)
    @DisplayName("Rotation ska inte tillåtas om den leder till krock med vägg")
    void rotateShouldNotBeAllowedWhenBlockedByWall() {
        GameBoard board = game.getBoard();
        Tetromino piece = game.getCurrentPiece();


        // Fyller hela brädet med blocks
        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int column = 0; column < GameBoard.COLUMNS; column++) {
                board.setCell(row, column, 7);
            }
        }

        // Skapar ett hål exakt efter den bit som spawnar så den är helt omsluten
        for (int i = 0; i < 4; i++) {
            board.setCell(piece.getY()[i], piece.getX()[i], 0);
        }

        int[] originalY = piece.getY().clone();
        int[] originalX = piece.getX().clone();

        game.rotate();

        // Verifiera att rotationen avbryts eftersom det inte finns plats att snurra
        assertArrayEquals(originalY, game.getCurrentPiece().getY(), "Y-koordinaterna ska vara oförrändrade");
        assertArrayEquals(originalX, game.getCurrentPiece().getX(), "X-koordinaterna ska vara oförrändrade");
    }

    @Test
    @DisplayName("TetrisGame ska uppdatera poäng när rader rensas via hardDrop")
    void gameShouldUpdateScoreOnLineClear() {
        GameBoard board = game.getBoard();

        // Skapar full rad längst ner
        for (int col = 0; col < GameBoard.COLUMNS; col++) {
            board.setCell(GameBoard.ROWS - 1, col, 1);
        }

        game.hardDrop(); // hardDrop() triggar lock > clearLines > addclearedLines

        assertTrue(game.getStats().getScore() > 0, "Poäng ska ha ökat");
    }

    @Test
    @DisplayName("reset() ska tömma brädet och nollställa game over")
    void resetShouldClearEverything() {
        game.getBoard().setCell(10, 5, 1);

        // Simulerar game over genom att blocka toppen
        game.getBoard().setCell(0,4,1);
        game.hardDrop(); // Triggar game over genom att spawna en bit som blir blockerad

        game.reset();

        // Verifiera full återställning
        assertFalse(game.isGameOver());
        assertEquals(0, game.getBoard().getCell(10,5));
    }

    @Test
    @DisplayName("moveDown vid botten ska låsa biten och skapa en ny")
    void moveDownAtBottomShouldLockAndSpawn() {
        Tetromino firstPiece = game.getCurrentPiece();

        // Trycker ned biten genom flera moveDown
        for (int i = 0; i < GameBoard.ROWS; i++) {
            game.moveDown();
        }

        Tetromino secondPiece = game.getCurrentPiece();
        assertNotSame(firstPiece, secondPiece, "En ny bit ska ha skapats efter att den första nått botten");
    }

    @Test
    @DisplayName("moveDown ska inte göra något om det är Game Over")
    void moveDownShouldNotWorkAfterGameOver() {
        // Forcerar Game Over
        game.getBoard().setCell(0,4,1);
        game.hardDrop();
        assertTrue(game.isGameOver());

        Tetromino pieceBefore = game.getCurrentPiece();
        int[] yBefore = pieceBefore.getY().clone();

        // Försöker flytta biten
        game.moveDown();

        assertArrayEquals(yBefore, game.getCurrentPiece().getY(), "Biten ska inte röra sig efter Game Over");
    }

    @RepeatedTest(100)
    @DisplayName("Rotation ska resultera i en giltig och tillåten position")
    void rotationShouldBeLegalAndWithinBounds() {
        game.rotate();

        // Kollar att koordinaterna efter rotationen faktiskt är giltiga
        for (int i = 0; i < 4; i++) {
            int x = game.getCurrentPiece().getX()[i];
            int y = game.getCurrentPiece().getY()[i];

            assertTrue(x >= 0 && x < GameBoard.COLUMNS, "X-koordinat utanför brädet efter rotation");
            assertTrue(y >= 0 && y < GameBoard.ROWS, "Y-koordinat utanför brädet efter rotation");
            assertEquals(0, game.getBoard().getCell(y, x), "Biten roterade in i ett annat block");
        }
    }
}
