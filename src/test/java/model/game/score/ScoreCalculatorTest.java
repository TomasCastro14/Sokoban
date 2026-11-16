package model.game.score;

import model.game.LevelStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    /** At most 10 moves, 5 pushes and 1 undo. */
    private static final LevelGoals GOALS = new LevelGoals(10, 5, 1);

    private final ScoreCalculator calculator = new StandardScoreCalculator();

    private static LevelStats stats(int moves, int pushes, int undos) {
        LevelStats stats = new LevelStats();
        for (int i = 0; i < moves; i++) {
            stats.addMove();
        }
        for (int i = 0; i < pushes; i++) {
            stats.addPush();
        }
        for (int i = 0; i < undos; i++) {
            stats.addUndo();
        }
        return stats;
    }

    @Test
    @DisplayName("1. Cada meta cumplida es una estrella")
    void oneStarPerMetGoal() {
        assertEquals(3, calculator.calculate(GOALS, stats(10, 5, 1), 60).stars());
        assertEquals(2, calculator.calculate(GOALS, stats(11, 5, 1), 60).stars());
        assertEquals(1, calculator.calculate(GOALS, stats(11, 6, 1), 60).stars());
        assertEquals(0, calculator.calculate(GOALS, stats(11, 6, 2), 60).stars());
    }

    @Test
    @DisplayName("2. El limite exacto cuenta como meta cumplida")
    void exactLimitIsMet() {
        ScoreResult result = calculator.calculate(GOALS, stats(10, 5, 1), 1);

        assertTrue(result.goals().stream().allMatch(GoalResult::isMet));
    }

    @Test
    @DisplayName("3. Puntaje: 5.000 por meta cumplida, multiplicado por los segundos restantes")
    void scoreIsGoalPointsTimesSecondsLeft() {
        // 2 metas x 5.000 x 50 s = 500.000
        assertEquals(500_000, calculator.calculate(GOALS, stats(11, 5, 1), 50).score());
        assertEquals(3 * StandardScoreCalculator.POINTS_PER_GOAL * 10,
                calculator.calculate(GOALS, stats(0, 0, 0), 10).score());
    }

    @Test
    @DisplayName("4. Sin metas cumplidas o sin tiempo restante el puntaje es 0")
    void zeroWithoutGoalsOrTime() {
        assertEquals(0, calculator.calculate(GOALS, stats(99, 99, 99), 100).score());
        assertEquals(0, calculator.calculate(GOALS, stats(0, 0, 0), 0).score());
    }

    @Test
    @DisplayName("5. El resultado detalla cada meta en orden: movimientos, empujes, undos")
    void resultListsEachGoal() {
        List<GoalResult> goals = calculator.calculate(GOALS, stats(12, 3, 0), 30).goals();

        assertEquals(List.of(GoalType.MOVES, GoalType.PUSHES, GoalType.UNDOS),
                goals.stream().map(result -> result.goal().type()).toList());
        assertEquals(List.of(12, 3, 0), goals.stream().map(GoalResult::achieved).toList());
        assertEquals(List.of(false, true, true), goals.stream().map(GoalResult::isMet).toList());
    }

    @Test
    @DisplayName("6. Segundos restantes negativos se rechazan")
    void negativeSecondsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(GOALS, stats(0, 0, 0), -1));
    }

    @ParameterizedTest(name = "{0}: en el limite {1} estrellas, uno mas {2}")
    @CsvSource({
            "MOVES,  3, 2",
            "PUSHES, 3, 2",
            "UNDOS,  3, 2"})
    @DisplayName("7. Borde de cada umbral por separado: el limite exacto suma, uno mas resta una estrella")
    void eachThresholdBorder(GoalType type, int atLimit, int overLimit) {
        assertEquals(atLimit, calculator.calculate(GOALS, statsAt(type, 0), 60).stars());
        assertEquals(overLimit, calculator.calculate(GOALS, statsAt(type, 1), 60).stars());
    }

    @Test
    @DisplayName("8. Un umbral en 0: sin usar cumple, usar uno solo ya no")
    void zeroLimit() {
        LevelGoals noUndos = new LevelGoals(10, 5, 0);

        assertEquals(3, calculator.calculate(noUndos, stats(10, 5, 0), 60).stars());
        assertEquals(2, calculator.calculate(noUndos, stats(10, 5, 1), 60).stars());
    }

    @Test
    @DisplayName("9. Las estrellas no dependen del tiempo: con 0 segundos se ganan igual (el puntaje es 0)")
    void starsDoNotDependOnTime() {
        ScoreResult result = calculator.calculate(GOALS, stats(10, 5, 1), 0);

        assertEquals(3, result.stars());
        assertEquals(0, result.score());
    }

    /** Every goal exactly at its limit, plus {@code extra} on the given one. */
    private static LevelStats statsAt(GoalType type, int extra) {
        return stats(10 + (type == GoalType.MOVES ? extra : 0),
                5 + (type == GoalType.PUSHES ? extra : 0),
                1 + (type == GoalType.UNDOS ? extra : 0));
    }
}
