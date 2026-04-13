import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.gritacademy.tetris.GameStats;

import static org.junit.jupiter.api.Assertions.*;

public class GameStatsTest {

    private static final long BASE = 500_000_000L;
    private static final long MIN = 100_000_000L;
    private static final long STEP = 50_000_000L;

    private GameStats stats;

    @BeforeEach
    void setUp() {
        stats = new GameStats(BASE, MIN, STEP);
    }

    @Test
    @DisplayName("addClearedLines ska uppdatera poäng och antal rensade rader")
    void addClearedLinesShouldUpdateScoreAndLines() {
        stats.addClearedLines(1);

        assertEquals(100, stats.getScore(), "En rad ska ge 100 poäng");
        assertEquals(1, stats.getTotalLinesCleared());
        assertEquals(1, stats.getLevel(), "Ska fortfarande vara level 1");
        assertEquals(BASE, stats.getCurrentDropIntervalNs(), "Hastigheten ska vara densamma");
    }

    @Test
    @DisplayName("Level ska öka varje 10 rader och drop intervallet ska minska")
    void levelShouldIncreaseEveryTenLinesAndDropIntervalShouldDecrease() {
        // Simulerar 10 radrensningar
        for (int i = 0; i < 10; i++) {
            stats.addClearedLines(1);
        }

        // Kollar att svårighetsgraden ökar
        assertEquals(10, stats.getTotalLinesCleared());
        assertEquals(2, stats.getLevel(), "Level ska öka vid 10 rader");
        assertEquals(BASE - STEP, stats.getCurrentDropIntervalNs(), "Hastigheten ska ha ökat");
    }

    @Test
    @DisplayName("Drop inteval ska aldrig gå under minimum")
    void dropIntervalShouldNeverGetBelowMinimum() {
        // Rensar 800 rader bara för att testa maxhastighet
        for(int i = 0; i < 200; i++) {
            stats.addClearedLines(4);
        }

        assertEquals(MIN, stats.getCurrentDropIntervalNs(), "Intervallet ska stanna på MIN-värde (100ms)");
        assertTrue(stats.getCurrentDropIntervalNs() >= MIN, "Får inte gå under minimigränsen");
    }

    @Test
    @DisplayName("Reset ska återställa score, level och dropinterval")
    void resetShouldRestoreStats() {
        // Lägger till lite poäng och levlar
        stats.addClearedLines(10);
        // Rensar allt
        stats.reset();

        // Kollar att allt är tillbaka på startvärden
        assertEquals(0, stats.getScore());
        assertEquals(0, stats.getTotalLinesCleared());
        assertEquals(1, stats.getLevel());
        assertEquals(BASE, stats.getCurrentDropIntervalNs());
    }

    @Test
    @DisplayName("Olika antal rader ska ge korrekt poäng enligt stegen")
    void differentLineClearsShouldGiveCorrectPoints() {
        // Kontrollerar att rätt antal radrensningar ger rätt poäng
        stats.addClearedLines(1); // +100
        assertEquals(100, stats.getScore());

        stats.addClearedLines(2); // +300 (total 400)
        assertEquals(400, stats.getScore());

        stats.addClearedLines(3); // +500 (total 900)
        assertEquals(900, stats.getScore());

        stats.addClearedLines(4); // +800 (total 1700)
        assertEquals(1700, stats.getScore());
    }

    @Test
    @DisplayName("Level ska inte öka förrän exakt 10 rader har rensats")
    void levelShouldChangeExactlyAtTenLines() {
        // Testar gränsvärde precis innan level up
        for(int i = 0; i < 9; i++) {
            stats.addClearedLines(1);
        }
        assertEquals(1, stats.getLevel(), "Ska fortfarande vara lvl 1 vid 9 rader");

        // Lägger till sista raden för att öka level
        stats.addClearedLines(1);
        assertEquals(2, stats.getLevel(), "ska nu vara lvl 2 vid exakt 10 rader");
    }

    @Test
    @DisplayName("addClearedLines ska ignorera 0 eller negativa värden")
    void addClearedLinesShouldIgnoreInvalidInput() {
        // Testar att logiken inte krashar för felaktig input
        stats.addClearedLines(0);
        stats.addClearedLines(-5);

        assertEquals(0, stats.getScore(), "Score ska inte ändras vid felaktig input");
        assertEquals(0, stats.getTotalLinesCleared(), "Antal rader ska inte heller ändras");
        assertEquals(1, stats.getLevel(), "Level ska inte heller ändras");
    }
}
