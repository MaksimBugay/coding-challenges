package com.interview;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

class MockDepartmentServiceContract extends DepartmentServiceContract {

  private static final DepartmentState PLATFORM =
      new DepartmentState(3L, 2L, 3000L, "Platform");

  private static final Map<String, Observation> RESPONSES = Map.ofEntries(
      Map.entry("TEST-001", observed(true, 2L, 1L, 2000L, "Engineering")),
      Map.entry("TEST-002", observed(true, PLATFORM)),
      Map.entry("TEST-003", observed(false, PLATFORM)),
      Map.entry("TEST-004", observed(false, PLATFORM)),
      Map.entry("TEST-005", observed(false, PLATFORM)),
      Map.entry("TEST-006", observed(false, 1L, null, 1000L, "Root A")),
      Map.entry("TEST-007", observed(false, PLATFORM)),
      Map.entry("TEST-008", observed(false, PLATFORM)),
      Map.entry("TEST-009", observed(false, 2L, 1L, 2000L, "Engineering")),
      Map.entry("TEST-010", saved(3L, 5L, 3000L, "Platform")),
      Map.entry("TEST-011", saved(3L, 6L, 3000L, "Platform")),
      Map.entry("TEST-012", observed(false, null)),
      Map.entry("TEST-013", observed(false, null)),
      Map.entry("TEST-013-MANAGER", observed(false, PLATFORM)),
      Map.entry("TEST-014", observed(false, null)),
      Map.entry("TEST-015", observed(false, PLATFORM)),
      Map.entry("TEST-016", observed(false, 3L, 999L, 3000L, "Platform")),
      Map.entry("TEST-017", observed(false, PLATFORM)),
      Map.entry("TEST-018", observed(false, PLATFORM)),
      Map.entry("TEST-019", observed(false, PLATFORM)),
      Map.entry("TEST-020", observed(false, PLATFORM)));

  @Override
  protected Operation operationFor(String scenarioId) {
    Observation response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }

    Operation operation = mock(Operation.class);
    when(operation.invoke(any(Input.class))).thenReturn(response);
    return operation;
  }

  private static Observation observed(boolean result, DepartmentState target) {
    return new Observation(result, target, 0, false, true);
  }

  private static Observation observed(
      boolean result, Long id, Long parentId, Long managerId, String title) {
    return observed(result, new DepartmentState(id, parentId, managerId, title));
  }

  private static Observation saved(Long id, Long parentId, Long managerId, String title) {
    return new Observation(
        true,
        new DepartmentState(id, parentId, managerId, title),
        1,
        true,
        true);
  }
}
