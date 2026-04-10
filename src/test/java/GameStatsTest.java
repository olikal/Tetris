import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.gritacademy.tetris.GameStats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameStatsTest {

    private static final long BASE = 500_000_000L;
    private static final long MIN = 100_000_000L;
    private static final long STEP = 50_000_000L;

    @Test
    @DisplayName("addClearedLines ska uppdatera poäng och antal rensade rader")
    void addClearedLinesShouldUpdateScoreAndLines() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        stats.addClearedLines(1);

        assertEquals(100, stats.getScore());
        assertEquals(1, stats.getTotalLinesCleared());
        assertEquals(1, stats.getLevel());
        assertEquals(BASE, stats.getCurrentDropIntervalNs());
    }

    @Test
    @DisplayName("Level ska öka varje 10 rader och drop intervallet ska minska")
    void levelShouldIncreaseEveryTenLinesAndDropIntervalShouldDecrease() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        for (int i = 0; i < 10; i++) {
            stats.addClearedLines(1);
        }

        assertEquals(10, stats.getTotalLinesCleared());
        assertEquals(2, stats.getLevel());
        assertEquals(BASE - STEP, stats.getCurrentDropIntervalNs());
    }

    @Test
    @DisplayName("Drop inteval ska aldrig gå under minimum")
    void dropIntervalShouldNeverGetBelowMinimum() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        for(int i = 0; i < 200; i++) {
            stats.addClearedLines(4);
        }

        assertEquals(MIN, stats.getCurrentDropIntervalNs());
        assertTrue(stats.getCurrentDropIntervalNs() >= MIN);
    }

    @Test
    @DisplayName("Reset ska återställa score, level och dropinterval")
    void resetShouldRestoreStats() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        stats.addClearedLines(10);
        stats.reset();

        assertEquals(0, stats.getScore());
        assertEquals(0, stats.getTotalLinesCleared());
        assertEquals(1, stats.getLevel());
        assertEquals(BASE, stats.getCurrentDropIntervalNs());
    }

    @Test
    @DisplayName("Olika antal rader ska ge korrekt poäng enligt stegen")
    void differentLineClearsShouldGiveCorrectPoints() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        stats.addClearedLines(1); // +100
        assertEquals(100, stats.getScore());

        stats.addClearedLines(2); // +300
        assertEquals(400, stats.getScore());

        stats.addClearedLines(3); // +500
        assertEquals(900, stats.getScore());

        stats.addClearedLines(4); // +800
        assertEquals(1700, stats.getScore());
    }

    @Test
    @DisplayName("Level ska inte öka förrän exakt 10 rader har rensats")
    void levelShouldChangeExactlyAtTenLines() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        for(int i = 0; i < 9; i++) {
            stats.addClearedLines(1);
        }
        assertEquals(1, stats.getLevel(), "Ska fortfarande vara lvl 1 vid 9 rader");

        stats.addClearedLines(1);
        assertEquals(2, stats.getLevel(), "ska nu vara lvl 2 vid exakt 10 rader");
    }

    @Test
    @DisplayName("addClearedLines ska ignorera 0 eller negativa värden")
    void addClearedLinesShouldIgnoreInvalidInput() {
        GameStats stats = new GameStats(BASE, MIN, STEP);

        stats.addClearedLines(0);
        stats.addClearedLines(-5);

        assertEquals(0, stats.getScore(), "Score ska inte ändras vid felaktig input");
        assertEquals(0, stats.getTotalLinesCleared(), "Antal rader ska inte heller ändras");
        assertEquals(1, stats.getLevel(), "Level ska inte heller ändras");
    }
}
