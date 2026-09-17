package bmv.prefix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

abstract class LongestCommonPrefixContract {

  protected abstract LongestCommonPrefix subjectFor(String scenarioId);

  @Test
  void test001FindsSharedPrefixFromRequirementExample() {
    verify(new PrefixCase(
        "TEST-001", new String[] {"flower", "flow", "flight"}, "fl"));
  }

  @Test
  void test002ReturnsEmptyForRequirementExampleWithoutCommonPrefix() {
    verify(new PrefixCase(
        "TEST-002", new String[] {"dog", "racecar", "car"}, ""));
  }

  @Test
  void test003ReturnsEmptyWhenFirstCharactersDiffer() {
    verify(new PrefixCase("TEST-003", new String[] {"a", "b"}, ""));
  }

  @Test
  void test004ReturnsTheOnlyString() {
    verify(new PrefixCase("TEST-004", new String[] {"prefix"}, "prefix"));
  }

  @Test
  void test005ReturnsEmptyWhenAnElementIsEmpty() {
    verify(new PrefixCase("TEST-005", new String[] {"abc", "", "ab"}, ""));
  }

  @Test
  void test006ReturnsTheShortestStringWhenItPrefixesEveryElement() {
    verify(new PrefixCase(
        "TEST-006", new String[] {"inter", "internet", "internal"}, "inter"));
  }

  @Test
  void test007StopsAtTheFirstDifferingPosition() {
    verify(new PrefixCase("TEST-007", new String[] {"abc", "abd", "abz"}, "ab"));
  }

  @Test
  void test008SupportsOneStringAtTheMaximumLength() {
    String twoHundredAs = "a".repeat(200);
    verify(new PrefixCase("TEST-008", new String[] {twoHundredAs}, twoHundredAs));
  }

  @Test
  void test009SupportsTheMaximumArrayAndStringLengths() {
    String twoHundredAs = "a".repeat(200);
    String[] twoHundredStrings = new String[200];
    Arrays.fill(twoHundredStrings, twoHundredAs);

    verify(new PrefixCase("TEST-009", twoHundredStrings, twoHundredAs));
  }

  @Test
  void test010InvokesThePublicBoundaryWithALiteralCase() {
    verify(new PrefixCase("TEST-010", new String[] {"same", "sample"}, "sam"));
  }

  private void verify(PrefixCase scenario) {
    String actual = subjectFor(scenario.id()).longestCommonPrefix(scenario.input());

    assertEquals(scenario.expected(), actual, scenario.id());
  }

  private record PrefixCase(String id, String[] input, String expected) {}
}
