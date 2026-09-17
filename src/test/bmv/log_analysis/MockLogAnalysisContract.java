package bmv.log_analysis;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class MockLogAnalysisContract extends LogAnalysisContract {

  private static final Map<String, List<Integer>> RESPONSES = Map.ofEntries(
      Map.entry("TEST-001", List.of(1, 2)),
      Map.entry("TEST-002", List.of(3, 5, 5)),
      Map.entry("TEST-003", List.of(2)),
      Map.entry("TEST-004", List.of(2)),
      Map.entry("TEST-005", List.of(0, 3)),
      Map.entry("TEST-006", List.of(1, 2, 1)),
      Map.entry("TEST-007", List.of(0)),
      Map.entry("TEST-008", Collections.nCopies(1_000, 0)),
      Map.entry("TEST-009", List.of(1)));

  @Override
  protected Operation operationFor(String scenarioId) {
    List<Integer> response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }

    Operation operation = mock(Operation.class);
    when(operation.invoke(anyInt(), anyList(), anyList(), anyInt())).thenReturn(response);
    return operation;
  }
}
