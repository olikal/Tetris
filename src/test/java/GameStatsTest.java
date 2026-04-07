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
}
