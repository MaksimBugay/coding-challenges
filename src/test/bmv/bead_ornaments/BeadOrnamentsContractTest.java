package bmv.bead_ornaments;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class BeadOrnamentsContractTest {
    protected abstract int beadOrnaments(List<Integer> beadCounts);

    @Test
    void test001ReturnsAllPublishedSampleResults() {
        assertAll(
                () -> assertEquals(2, beadOrnaments(List.of(2, 1))),
                () -> assertEquals(4, beadOrnaments(List.of(2, 2))),
                () -> assertEquals(16, beadOrnaments(List.of(4))),
                () -> assertEquals(9, beadOrnaments(List.of(3, 1))),
                () -> assertEquals(125, beadOrnaments(List.of(1, 1, 1, 1, 1)))
        );
    }

    @Test
    void test002DoesNotMutateCallerOwnedInput() {
        var input = new ArrayList<>(List.of(2, 1));
        var before = List.copyOf(input);

        beadOrnaments(input);

        assertEquals(before, input);
    }

    @Test
    void test003AppliesModulusAtMaximumConstraintValues() {
        assertEquals(409_191_278, beadOrnaments(List.of(
                30, 30, 30, 30, 30, 30, 30, 30, 30, 30)));
    }

    @Test
    void test004HandlesContractualBoundaryCases() {
        assertAll(
                () -> assertEquals(1, beadOrnaments(List.of(1))),
                () -> assertEquals(428_755_556, beadOrnaments(List.of(30))),
                () -> assertEquals(100_000_000,
                        beadOrnaments(List.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)))
        );
    }
}
