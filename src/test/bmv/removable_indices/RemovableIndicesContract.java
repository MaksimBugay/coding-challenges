package bmv.removable_indices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

abstract class RemovableIndicesContract {

  @FunctionalInterface
  protected interface Operation {
    List<Integer> invoke(String str1, String str2);
  }

  protected abstract Operation operationFor(String scenarioId);

  @Test
  void test001ReturnsEveryIndexInTheRequiredRepeatedRunExample() {
    verify(new RemovalCase("TEST-001", "abdgggda", "abdggda", List.of(3, 4, 5)));
  }

  @Test
  void test002ReturnsTheUniqueMiddleRemoval() {
    verify(new RemovalCase("TEST-002", "abc", "ac", List.of(1)));
  }

  @Test
  void test003ReturnsRepeatedRemovalsAtTheBeginningInOrder() {
    verify(new RemovalCase("TEST-003", "aabc", "abc", List.of(0, 1)));
  }

  @Test
  void test004ReturnsRepeatedRemovalsAtTheEndInOrder() {
    verify(new RemovalCase("TEST-004", "abcc", "abc", List.of(2, 3)));
  }

  @Test
  void test005RemovesTheFinalCharacter() {
    verify(new RemovalCase("TEST-005", "abcd", "abc", List.of(3)));
  }

  @Test
  void test006SupportsMinimumLengthsWithEveryPositionRemovable() {
    verify(new RemovalCase("TEST-006", "aa", "a", List.of(0, 1)));
  }

  @Test
  void test007ReturnsOnlyMinusOneWhenRemovalIsImpossible() {
    verify(new RemovalCase("TEST-007", "abc", "de", List.of(-1)));
  }

  @Test
  void test008SupportsTheMaximumInputLengthWithOneRemoval() {
    verify(new RemovalCase(
        "TEST-008",
        "a".repeat(199_999) + "b",
        "a".repeat(199_999),
        List.of(199_999)));
  }

  @Test
  void test009ReturnsEveryIndexAtTheMaximumResultSize() {
    List<Integer> allIndexes = IntStream.range(0, 200_000).boxed().toList();
    verify(new RemovalCase(
        "TEST-009",
        "a".repeat(200_000),
        "a".repeat(199_999),
        allIndexes));
  }

  @Test
  void test010InvokesTheExactPublicOperationWithALiteralCase() {
    verify(new RemovalCase("TEST-010", "xyyz", "xyz", List.of(1, 2)));
  }

  private void verify(RemovalCase scenario) {
    List<Integer> actual = operationFor(scenario.id()).invoke(scenario.str1(), scenario.str2());

    assertNotNull(actual, scenario.id());
    assertEquals(scenario.expected(), actual, scenario.id());
  }

  private record RemovalCase(
      String id,
      String str1,
      String str2,
      List<Integer> expected) {}
}
