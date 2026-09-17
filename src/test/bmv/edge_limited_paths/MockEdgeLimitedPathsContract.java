package bmv.edge_limited_paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

class MockEdgeLimitedPathsContract extends EdgeLimitedPathsContract {

  private static final Map<String, boolean[]> RESPONSES = Map.ofEntries(
      Map.entry("TEST-001", new boolean[] {false, true}),
      Map.entry("TEST-002", new boolean[] {false}),
      Map.entry("TEST-003", new boolean[] {true}),
      Map.entry("TEST-004", new boolean[] {false}),
      Map.entry("TEST-005", new boolean[] {true}),
      Map.entry("TEST-006", new boolean[] {true, false}),
      Map.entry("TEST-007", new boolean[] {true, false, true}),
      Map.entry("TEST-008", new boolean[] {true, true}),
      Map.entry("TEST-009", new boolean[] {true, false}),
      Map.entry("TEST-010", new boolean[] {true, false}),
      Map.entry("TEST-011", new boolean[] {true}),
      Map.entry("TEST-012", new boolean[] {false, false}));

  @Override
  protected Operation operationFor(String scenarioId) {
    boolean[] response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }

    Operation operation = mock(Operation.class);
    when(operation.invoke(anyInt(), any(int[][].class), any(int[][].class)))
        .thenReturn(response);
    return operation;
  }
}
