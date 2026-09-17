package bmv.longest_increasing_subsequence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class LongestIncreasingSubsequenceContract {
    @FunctionalInterface
    protected interface Operation {
        int apply(List<Integer> values);
    }

    protected abstract Operation operationFor(String scenarioId);

    @Test
    void test001_singletonHasLengthOne() {
        assertResult("TEST-001", List.of(7), 1);
    }

    @Test
    void test002_sampleZeroHasLengthThree() {
        assertResult("TEST-002", List.of(2, 7, 4, 3, 8), 3);
    }

    @Test
    void test003_sampleOneHasLengthFour() {
        assertResult("TEST-003", List.of(2, 4, 3, 7, 4, 5), 4);
    }

    @Test
    void test004_duplicateValuesDoNotExtendTheSubsequence() {
        assertResult("TEST-004", List.of(1, 3, 3, 4), 3);
    }

    @Test
    void test005_strictlyDecreasingHasLengthOne() {
        assertResult("TEST-005", List.of(5, 4, 3, 2, 1), 1);
    }

    @Test
    void test006_strictlyIncreasingHasLengthOfInput() {
        assertResult("TEST-006", List.of(1, 2, 3, 4, 5), 5);
    }

    @Test
    void test007_allEqualValuesHaveLengthOne() {
        assertResult("TEST-007", List.of(3, 3, 3, 3), 1);
    }

    @Test
    void test008_inputListRemainsUnchanged() {
        List<Integer> input = new ArrayList<>(List.of(2, 7, 4, 3, 8));
        List<Integer> before = List.copyOf(input);

        assertEquals(3, operationFor("TEST-008").apply(input));
        assertEquals(before, input);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void test009_maximumSizeInputWithinValueDomainCompletesQuickly() {
        // n = 10^6 elements, each within the published 1..100000 value domain:
        // ten repetitions of the ascending block [1..100000]. Since a strictly
        // increasing subsequence can use each of the 100000 distinct values at
        // most once, the exact LIS length is 100000 regardless of repetition.
        List<Integer> repeatedAscendingBlocks = IntStream.range(0, 1_000_000)
                .map(i -> (i % 100_000) + 1)
                .boxed()
                .collect(Collectors.toList());

        assertResult("TEST-009", repeatedAscendingBlocks, 100_000);
    }

    private void assertResult(String scenarioId, List<Integer> input, int expected) {
        assertEquals(expected, operationFor(scenarioId).apply(input));
    }
}
