package bmv.insertion_sort_advanced_analysis;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class InsertionSortContract {
    @FunctionalInterface
    protected interface Operation {
        int apply(List<Integer> values);
    }

    protected abstract Operation operationFor(String scenarioId);

    @Test
    void test001_singleElementNeedsNoShift() {
        assertResult("TEST-001", List.of(7), 0);
    }

    @Test
    void test002_sortedValuesIncludingDuplicatesNeedNoShift() {
        assertResult("TEST-002", List.of(1, 1, 1, 2, 2), 0);
    }

    @Test
    void test003_sampleMixedValuesHaveFourShifts() {
        assertResult("TEST-003", List.of(2, 1, 3, 1, 2), 4);
    }

    @Test
    void test004_reverseExampleHasSixShifts() {
        assertResult("TEST-004", List.of(4, 3, 2, 1), 6);
    }

    @Test
    void test005_equalValuesAreNotInversions() {
        assertResult("TEST-005", List.of(2, 2, 1, 1), 4);
    }

    @Test
    void test006_resultUsesStarterRequiredIntNarrowing() {
        List<Integer> descending = IntStream.rangeClosed(1, 100_000)
                .map(i -> 100_001 - i)
                .boxed()
                .toList();

        assertResult("TEST-006", descending, 704_982_704);
    }

    @Test
    void test007_maximumOrderedInputCompletesWithZero() {
        List<Integer> ordered = IntStream.rangeClosed(1, 100_000).boxed().toList();

        assertResult("TEST-007", ordered, 0);
    }

    @Test
    void test008_inputListRemainsUnchanged() {
        List<Integer> input = new ArrayList<>(List.of(3, 1, 2, 1));
        List<Integer> before = List.copyOf(input);

        assertEquals(4, operationFor("TEST-008").apply(input));
        assertEquals(before, input);
    }

    private void assertResult(String scenarioId, List<Integer> input, int expected) {
        assertEquals(expected, operationFor(scenarioId).apply(input));
    }
}
