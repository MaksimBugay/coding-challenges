package bmv.insertion_sort_advanced_analysis;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockInsertionSortContract extends InsertionSortContract {
    private static final Map<String, Integer> RESPONSES = Map.of(
            "TEST-001", 0,
            "TEST-002", 0,
            "TEST-003", 4,
            "TEST-004", 6,
            "TEST-005", 4,
            "TEST-006", 704_982_704,
            "TEST-007", 0,
            "TEST-008", 4
    );

    @Override
    protected Operation operationFor(String scenarioId) {
        Operation operation = mock(Operation.class);
        when(operation.apply(anyList())).thenReturn(RESPONSES.get(scenarioId));
        return operation;
    }
}
