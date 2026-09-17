package bmv.prefix;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

class MockLongestCommonPrefixContract extends LongestCommonPrefixContract {

  private static final Map<String, String> RESPONSES = Map.ofEntries(
      Map.entry("TEST-001", "fl"),
      Map.entry("TEST-002", ""),
      Map.entry("TEST-003", ""),
      Map.entry("TEST-004", "prefix"),
      Map.entry("TEST-005", ""),
      Map.entry("TEST-006", "inter"),
      Map.entry("TEST-007", "ab"),
      Map.entry("TEST-008", "a".repeat(200)),
      Map.entry("TEST-009", "a".repeat(200)),
      Map.entry("TEST-010", "sam"));

  @Override
  protected LongestCommonPrefix subjectFor(String scenarioId) {
    String response = RESPONSES.get(scenarioId);
    if (response == null) {
      throw new IllegalArgumentException("No mock response for " + scenarioId);
    }

    LongestCommonPrefix subject = mock(LongestCommonPrefix.class);
    when(subject.longestCommonPrefix(any(String[].class))).thenReturn(response);
    return subject;
  }
}
