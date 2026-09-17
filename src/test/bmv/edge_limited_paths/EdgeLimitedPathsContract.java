package bmv.edge_limited_paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

abstract class EdgeLimitedPathsContract {

  @FunctionalInterface
  protected interface Operation {
    boolean[] invoke(int n, int[][] edgeList, int[][] queries);
  }

  protected abstract Operation operationFor(String scenarioId);

  @Test
  void test001AnswersTheFirstRequirementExample() {
    verify(new PathCase(
        "TEST-001",
        3,
        new int[][] {{0, 1, 2}, {1, 2, 4}, {2, 0, 8}, {1, 0, 16}},
        new int[][] {{0, 1, 2}, {0, 2, 5}},
        new boolean[] {false, true}));
  }

  @Test
  void test002ExcludesAnEdgeWhoseWeightEqualsTheLimit() {
    verify(new PathCase(
        "TEST-002",
        2,
        new int[][] {{0, 1, 2}},
        new int[][] {{0, 1, 2}},
        new boolean[] {false}));
  }

  @Test
  void test003FindsAQualifyingIndirectPath() {
    verify(new PathCase(
        "TEST-003",
        3,
        new int[][] {{0, 1, 2}, {1, 2, 4}},
        new int[][] {{0, 2, 5}},
        new boolean[] {true}));
  }

  @Test
  void test004ReturnsFalseForDisconnectedComponents() {
    verify(new PathCase(
        "TEST-004",
        4,
        new int[][] {{0, 1, 1}, {2, 3, 1}},
        new int[][] {{0, 3, 2}},
        new boolean[] {false}));
  }

  @Test
  void test005TraversesUndirectedEdgesInReverse() {
    verify(new PathCase(
        "TEST-005",
        3,
        new int[][] {{0, 1, 3}, {1, 2, 4}},
        new int[][] {{2, 0, 5}},
        new boolean[] {true}));
  }

  @Test
  void test006ReturnsOneNonNullResultPerQueryForTheSecondRequirementExample() {
    verify(new PathCase(
        "TEST-006",
        5,
        new int[][] {{0, 1, 10}, {1, 2, 5}, {2, 3, 9}, {3, 4, 13}},
        new int[][] {{0, 4, 14}, {1, 4, 13}},
        new boolean[] {true, false}));
  }

  @Test
  void test007PreservesTheOriginalOrderOfQueriesWithUnsortedLimits() {
    verify(new PathCase(
        "TEST-007",
        3,
        new int[][] {{0, 1, 2}, {1, 2, 4}},
        new int[][] {{0, 2, 5}, {0, 2, 4}, {0, 1, 3}},
        new boolean[] {true, false, true}));
  }

  @Test
  void test008AnswersDuplicateQueriesAtTheirOriginalIndexes() {
    verify(new PathCase(
        "TEST-008",
        3,
        new int[][] {{0, 1, 2}, {1, 2, 4}},
        new int[][] {{0, 2, 5}, {0, 2, 5}},
        new boolean[] {true, true}));
  }

  @Test
  void test009UsesTheQualifyingParallelEdgeAndKeepsTheLimitStrict() {
    verify(new PathCase(
        "TEST-009",
        2,
        new int[][] {{0, 1, 7}, {0, 1, 3}},
        new int[][] {{0, 1, 4}, {0, 1, 3}},
        new boolean[] {true, false}));
  }

  @Test
  void test010IgnoresAHeavyCycleEdge() {
    verify(new PathCase(
        "TEST-010",
        4,
        new int[][] {{0, 1, 1}, {1, 2, 2}, {2, 0, 100}, {2, 3, 3}},
        new int[][] {{0, 3, 4}, {0, 3, 3}},
        new boolean[] {true, false}));
  }

  @Test
  void test011SupportsMinimumValidDimensionsAndWeights() {
    verify(new PathCase(
        "TEST-011",
        2,
        new int[][] {{0, 1, 1}},
        new int[][] {{0, 1, 2}},
        new boolean[] {true}));
  }

  @Test
  void test012SupportsTheMaximumWeightAndLimitValues() {
    verify(new PathCase(
        "TEST-012",
        2,
        new int[][] {{0, 1, 1_000_000_000}},
        new int[][] {{0, 1, 1}, {0, 1, 1_000_000_000}},
        new boolean[] {false, false}));
  }

  private void verify(PathCase scenario) {
    boolean[] actual = operationFor(scenario.id())
        .invoke(scenario.n(), scenario.edgeList(), scenario.queries());

    assertNotNull(actual, scenario.id());
    assertEquals(scenario.queries().length, actual.length, scenario.id());
    assertArrayEquals(scenario.expected(), actual, scenario.id());
  }

  private record PathCase(
      String id,
      int n,
      int[][] edgeList,
      int[][] queries,
      boolean[] expected) {}
}
