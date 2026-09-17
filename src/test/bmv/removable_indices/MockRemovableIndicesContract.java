package bmv.removable_indices;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

class MockRemovableIndicesContract extends RemovableIndicesContract {

  private static final Map<String, List<Integer>> RESPONSES = Map.ofEntries(
      Map.entry("TEST-001", List.of(3, 4, 5)),
      Map.entry("TEST-002", List.of(1)),
      Map.entry("TEST-003", List.of(0, 1)),
      Map.entry("TEST-004", List.of(2, 3)),
      Map.entry("TEST-005", List.of(3)),
      Map.entry("TEST-006", List.of(0, 1)),
      Map.entry("TEST-007", List.of(-1)),
      Map.entry("TEST-008", List.of(199_999)),
      Map.entry("TEST-009", IntStream.range(0, 200_000).boxed().toList()),
      Map.entry("TEST-010", List.of(1, 2)));

  @Override
  protected Operation operationFor(String scenarioId) {
    List<Integer> response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }

    Operation operation = mock(Operation.class);
    when(operation.invoke(anyString(), anyString())).thenReturn(response);
    return operation;
  }
}
