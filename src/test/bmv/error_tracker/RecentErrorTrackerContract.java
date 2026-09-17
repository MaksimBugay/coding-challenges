package bmv.error_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

abstract class RecentErrorTrackerContract {

  @FunctionalInterface
  protected interface Operation {
    List<String> invoke(
        int k, int t, List<Integer> timestamps, List<String> errorCodes);
  }

  protected abstract Operation operationFor(String scenarioId);

  @Test
  void test001ReturnsBothCodesFromTheProblemExample() {
    verify(new TrackerCase(
        "TEST-001",
        2,
        10,
        List.of(100, 101, 102, 105, 110),
        List.of("E1", "E2", "E1", "E1", "E2"),
        List.of("E1", "E2")));
  }

  @Test
  void test002ReturnsOnlyE2FromSampleCase0() {
    verify(new TrackerCase(
        "TEST-002",
        3,
        5,
        List.of(1, 2, 4, 5, 6, 7, 10),
        List.of("E1", "E2", "E1", "E1", "E2", "E2", "E2"),
        List.of("E2")));
  }

  @Test
  void test003ReturnsOnlyE1FromSampleCase1() {
    verify(new TrackerCase(
        "TEST-003",
        2,
        4,
        List.of(1, 2, 4, 5, 6),
        List.of("E1", "E2", "E1", "E1", "E2"),
        List.of("E1")));
  }

  @Test
  void test004IncludesTheLowerBoundaryAndExcludesThePriorSecond() {
    verify(new TrackerCase(
        "TEST-004",
        2,
        5,
        List.of(5, 5, 6, 10),
        List.of("E2", "E2", "E1", "E1"),
        List.of("E1")));
  }

  @Test
  void test005ReturnsANonNullEmptyListWhenNoCodeQualifies() {
    verify(new TrackerCase(
        "TEST-005",
        2,
        2,
        List.of(1, 2, 3),
        List.of("E1", "E2", "E3"),
        List.of()));
  }

  @Test
  void test006IncludesEveryEntryAtTheLatestTimestampAndSortsTheCodes() {
    verify(new TrackerCase(
        "TEST-006",
        1,
        1,
        List.of(7, 8, 8),
        List.of("OLD", "B", "A"),
        List.of("A", "B")));
  }

  @Test
  void test007UsesNaturalOrderInsteadOfEncounterOrFrequencyOrder() {
    verify(new TrackerCase(
        "TEST-007",
        2,
        7,
        List.of(1, 2, 3, 4, 5, 6, 7),
        List.of("a1", "A2", "A10", "a1", "A2", "A10", "A10"),
        List.of("A10", "A2", "a1")));
  }

  @Test
  void test008SupportsTheSingleEntryMinimum() {
    verify(new TrackerCase(
        "TEST-008",
        1,
        1,
        List.of(1),
        List.of("E1"),
        List.of("E1")));
  }

  @Test
  void test009SupportsOneHundredThousandEntriesAtTheSameTimestamp() {
    List<Integer> timestamps = java.util.Collections.nCopies(100_000, 1);
    List<String> errorCodes = IntStream.range(0, 100_000)
        .mapToObj(index -> index % 2 == 0 ? "E2" : "E1")
        .toList();

    verify(new TrackerCase(
        "TEST-009",
        50_000,
        1,
        timestamps,
        errorCodes,
        List.of("E1", "E2")));
  }

  @Test
  void test010InvokesTheExactPublicOperationWithALiteralCase() {
    verify(new TrackerCase(
        "TEST-010",
        2,
        10,
        List.of(100, 101, 102, 105, 110),
        List.of("E1", "E2", "E1", "E1", "E2"),
        List.of("E1", "E2")));
  }

  private void verify(TrackerCase scenario) {
    List<String> actual = operationFor(scenario.id()).invoke(
        scenario.k(), scenario.t(), scenario.timestamps(), scenario.errorCodes());

    assertNotNull(actual, scenario.id());
    assertEquals(scenario.expected(), actual, scenario.id());
  }

  private record TrackerCase(
      String id,
      int k,
      int t,
      List<Integer> timestamps,
      List<String> errorCodes,
      List<String> expected) {}
}
