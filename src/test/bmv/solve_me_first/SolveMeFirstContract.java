package bmv.solve_me_first;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.IntBinaryOperator;
import org.junit.jupiter.api.Test;

abstract class SolveMeFirstContract {

  protected abstract IntBinaryOperator subjectFor(String scenarioId);

  @Test
  void test001AddsTheSampleInputs() {
    verify(new AdditionCase("TEST-001", 2, 3, 5));
  }

  @Test
  void test002AddsTheProblemExampleInputs() {
    verify(new AdditionCase("TEST-002", 7, 3, 10));
  }

  @Test
  void test003AddsTheMinimumInputs() {
    verify(new AdditionCase("TEST-003", 1, 1, 2));
  }

  @Test
  void test004AddsTheMaximumInputs() {
    verify(new AdditionCase("TEST-004", 1000, 1000, 2000));
  }

  private void verify(AdditionCase scenario) {
    int actual = subjectFor(scenario.id()).applyAsInt(scenario.a(), scenario.b());

    assertEquals(scenario.expected(), actual, scenario.id());
  }

  private record AdditionCase(String id, int a, int b, int expected) {}
}
