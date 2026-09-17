package bmv.log_analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

abstract class LogAnalysisContract {

  @FunctionalInterface
  protected interface Operation {
    List<Integer> invoke(
        int n, List<List<Integer>> logData, List<Integer> query, int x);
  }

  protected abstract Operation operationFor(String scenarioId);

  @Test
  void test001ReturnsTheStatementExampleCounts() {
    verify(new LogAnalysisCase(
        "TEST-001",
        3,
        List.of(List.of(3, 3), List.of(2, 6), List.of(1, 5)),
        List.of(10, 11),
        5,
        List.of(1, 2)));
  }

  @Test
  void test002HandlesUnsortedLogsAndQueriesFromSampleCase0() {
    verify(new LogAnalysisCase(
        "TEST-002",
        6,
        List.of(List.of(3, 2), List.of(4, 3), List.of(2, 6), List.of(6, 3)),
        List.of(3, 2, 6),
        2,
        List.of(3, 5, 5)));
  }

  @Test
  void test003IncludesBothBoundariesAndExcludesAdjacentTimes() {
    verify(new LogAnalysisCase(
        "TEST-003",
        4,
        List.of(List.of(1, 5), List.of(2, 10), List.of(3, 4), List.of(4, 11)),
        List.of(10),
        5,
        List.of(2)));
  }

  @Test
  void test004CountsARepeatedServerOnlyOnce() {
    verify(new LogAnalysisCase(
        "TEST-004",
        3,
        List.of(List.of(1, 4), List.of(1, 5), List.of(1, 5)),
        List.of(5),
        1,
        List.of(2)));
  }

  @Test
  void test005ReturnsZeroForAllActiveAndNForNoneActive() {
    verify(new LogAnalysisCase(
        "TEST-005",
        3,
        List.of(List.of(1, 5), List.of(2, 5), List.of(3, 5)),
        List.of(5, 10),
        1,
        List.of(0, 3)));
  }

  @Test
  void test006PreservesDuplicateUnsortedQueryPositions() {
    verify(new LogAnalysisCase(
        "TEST-006",
        3,
        List.of(List.of(1, 3), List.of(2, 8), List.of(3, 8)),
        List.of(8, 3, 8),
        1,
        List.of(1, 2, 1)));
  }

  @Test
  void test007SupportsTheSingleServerLogAndQueryMinimum() {
    verify(new LogAnalysisCase(
        "TEST-007",
        1,
        List.of(List.of(1, 1)),
        List.of(1),
        1,
        List.of(0)));
  }

  @Test
  void test008SupportsMaximumServerLogAndQueryCounts() {
    List<List<Integer>> logData = IntStream.rangeClosed(1, 1_000)
        .mapToObj(serverId -> List.of(serverId, 100_000))
        .toList();

    verify(new LogAnalysisCase(
        "TEST-008",
        1_000,
        logData,
        Collections.nCopies(1_000, 100_000),
        1,
        Collections.nCopies(1_000, 0)));
  }

  @Test
  void test009InvokesTheExactPublicOperationWithALiteralCase() {
    verify(new LogAnalysisCase(
        "TEST-009",
        2,
        List.of(List.of(1, 1)),
        List.of(1),
        1,
        List.of(1)));
  }

  private void verify(LogAnalysisCase scenario) {
    List<Integer> actual = operationFor(scenario.id()).invoke(
        scenario.n(), scenario.logData(), scenario.query(), scenario.x());

    assertNotNull(actual, scenario.id());
    assertEquals(scenario.expected(), actual, scenario.id());
  }

  private record LogAnalysisCase(
      String id,
      int n,
      List<List<Integer>> logData,
      List<Integer> query,
      int x,
      List<Integer> expected) {}
}
