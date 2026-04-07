package se.gritacademy.tetris;

public class GameStats {

    private int score;
    private int level;
    private int totalLinesCleared;
    private long currentDropIntervalNs; // Antal nano-sekunder mellan varje automatisk drop

    private final long baseDropIntervalNs; // Start-drop rate (500 000 000ns = 0,5 sec per tick)
    private final long minDropIntervalNs; // Drop rate-cap. 0,1 sec per tick
    private final long dropDecreasePerLevelNs; // 0,05 sec minskning per level


    // Tar in alla tre värden utifrån (från TetrisController via TetrisGame)
    public GameStats(long baseDropIntervalNs, long minDropIntervalNs, long dropDecreasePerLevelNs) {
        this.minDropIntervalNs = minDropIntervalNs;
        this.dropDecreasePerLevelNs = dropDecreasePerLevelNs;
        this.baseDropIntervalNs = baseDropIntervalNs;
        reset(); // Kör reset för att återställa alla värden
    }

    // Återställer score, level, linescleared och dropinterval till startvärdena
    public void reset() {
        score = 0;
        level = 1;
        totalLinesCleared = 0;
        currentDropIntervalNs = baseDropIntervalNs;
    }

    // Körs när bitar låses fast och clearFullLines returnerar värde på mer än 0
    public void addClearedLines(int clearedLines) {
        if (clearedLines <= 0) {
            return;
        }

        // Poäng för rensade rader. Fler rader = högre poäng
        int points = switch (clearedLines) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };

        // Uppdaterar stats och svårighet
        score += points;
        totalLinesCleared += clearedLines;
        updateDifficulty();
    }

    // Ökar svårighetsgrad för varje 10 rensade rader
    private void updateDifficulty() {
        int newLevel = 1 + (totalLinesCleared / 10);

        // Kollar om newlevel (10 rader) uppnåts, isf öka level
        if (newLevel > level) {
            level = newLevel;

            // Reducerar drop interval med 0,05 sek per level increase
            long newInterval = currentDropIntervalNs - dropDecreasePerLevelNs;
            // Tills lägsta droprate är uppnådd
            if (newInterval < minDropIntervalNs) {
                newInterval = minDropIntervalNs;
            }
            // Sätter ny droprate
            currentDropIntervalNs = newInterval;
        }
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    public long getCurrentDropIntervalNs() {
        return currentDropIntervalNs;
    }
}
