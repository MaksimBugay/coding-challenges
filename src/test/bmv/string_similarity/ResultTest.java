package bmv.string_similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Black-box contract tests for {@link Result#stringSimilarity(String)}.
 *
 * <p>Expected values are derived from
 * {@code ai/string_similarity/api-contract.md} (API-001) and
 * {@code ai/string_similarity/requirements.md} (REQ-001), not from the
 * production algorithm. Against the design-phase placeholder these tests
 * are expected to fail (it throws {@link UnsupportedOperationException});
 * that failure demonstrates the suite actually exercises the contract.
 */
class ResultTest {

    /** TEST-001 / AC-001-01: task.md sample case 1. */
    @Test
    void stringSimilarity_sample1_returnsElevenSample() {
        assertEquals(11, Result.stringSimilarity("ababaa"));
    }

    /** TEST-002 / AC-001-02: task.md sample case 2. */
    @Test
    void stringSimilarity_sample2_returnsThree() {
        assertEquals(3, Result.stringSimilarity("aa"));
    }

    /** TEST-003 / AC-001-03: single-character boundary. */
    @Test
    void stringSimilarity_singleCharacter_returnsOne() {
        assertEquals(1, Result.stringSimilarity("a"));
    }

    /** TEST-004 / AC-001-04: all-identical-character boundary (4+3+2+1). */
    @Test
    void stringSimilarity_allIdenticalCharacters_returnsTriangularSum() {
        assertEquals(10, Result.stringSimilarity("aaaa"));
    }

    /** TEST-005 / AC-001-05: no proper suffix shares a prefix (5+0+0+0+0). */
    @Test
    void stringSimilarity_noSharedPrefixes_returnsLengthOnly() {
        assertEquals(5, Result.stringSimilarity("abcde"));
    }

    /**
     * TEST-006 / AC-001-06: guards against an accidental O(n^2) implementation.
     *
     * <p>Uses n=20000 identical characters (worst case for a naive
     * suffix-by-suffix comparison) so the expected sum (n(n+1)/2 = 200010000)
     * stays well within {@code int} range, deliberately avoiding the
     * near-{@code Integer.MAX_VALUE} boundary discussed in
     * {@code ai/string_similarity/requirements.md} (Q-001), which has no
     * contractually assertable expected value at n=100000.
     */
    @Test
    void stringSimilarity_largeIdenticalInput_completesWithinTimeBound() {
        String s = "a".repeat(20_000);
        int result = assertTimeoutPreemptively(
                Duration.ofSeconds(2), () -> Result.stringSimilarity(s));
        assertEquals(200_010_000, result);
    }
}
