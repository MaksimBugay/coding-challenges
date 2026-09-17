package bmv.longest_increasing_subsequence;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockLongestIncreasingSubsequenceContractTest extends LongestIncreasingSubsequenceContract {
    private static final Map<String, Integer> RESPONSES = Map.ofEntries(
            Map.entry("TEST-001", 1),
            Map.entry("TEST-002", 3),
            Map.entry("TEST-003", 4),
            Map.entry("TEST-004", 3),
            Map.entry("TEST-005", 1),
            Map.entry("TEST-006", 5),
            Map.entry("TEST-007", 1),
            Map.entry("TEST-008", 3),
            Map.entry("TEST-009", 100_000)
    );

    @Override
    protected Operation operationFor(String scenarioId) {
        Operation operation = mock(Operation.class);
        when(operation.apply(anyList())).thenReturn(RESPONSES.get(scenarioId));
        return operation;
    }
}
