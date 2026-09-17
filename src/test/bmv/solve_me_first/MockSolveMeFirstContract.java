package bmv.solve_me_first;

import java.util.Map;
import java.util.function.IntBinaryOperator;

class MockSolveMeFirstContract extends SolveMeFirstContract {

  private static final Map<String, Integer> RESPONSES = Map.of(
      "TEST-001", 5,
      "TEST-002", 10,
      "TEST-003", 2,
      "TEST-004", 2000);

  @Override
  protected IntBinaryOperator subjectFor(String scenarioId) {
    Integer response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }
    return (a, b) -> response;
  }
}
