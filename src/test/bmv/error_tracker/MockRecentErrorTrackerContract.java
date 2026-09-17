package bmv.error_tracker;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

class MockRecentErrorTrackerContract extends RecentErrorTrackerContract {

  private static final Map<String, List<String>> RESPONSES = Map.ofEntries(
      Map.entry("TEST-001", List.of("E1", "E2")),
      Map.entry("TEST-002", List.of("E2")),
      Map.entry("TEST-003", List.of("E1")),
      Map.entry("TEST-004", List.of("E1")),
      Map.entry("TEST-005", List.of()),
      Map.entry("TEST-006", List.of("A", "B")),
      Map.entry("TEST-007", List.of("A10", "A2", "a1")),
      Map.entry("TEST-008", List.of("E1")),
      Map.entry("TEST-009", List.of("E1", "E2")),
      Map.entry("TEST-010", List.of("E1", "E2")));

  @Override
  protected Operation operationFor(String scenarioId) {
    List<String> response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }

    Operation operation = mock(Operation.class);
    when(operation.invoke(anyInt(), anyInt(), anyList(), anyList())).thenReturn(response);
    return operation;
  }
}
