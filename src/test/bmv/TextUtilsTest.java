package bmv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bmv.textstat.TextUtils;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextUtilsTest {

  private String line = "Quick brown Fox jumped over a fat lazy Dog";

  @Test
  void getWordsTest() {
    String[] words = TextUtils.getWords(line);
    assertEquals(9, words.length);
    assertEquals("jumped", words[3]);
  }

  @Test
  void isStartedWithCapitalLetterTest() {
    Assertions.assertTrue(TextUtils.isStartedWithCapitalLetter("House"));
    Assertions.assertFalse(TextUtils.isStartedWithCapitalLetter("push"));
  }

  @Test
  void transformAndFilterTest() {
    List<String> input = List.of(TextUtils.getWords(line));

    List<String> output = TextUtils.transformAndFilter(input, List.of("a", "an"), false);
    assertEquals(8, output.size());

    output = TextUtils.transformAndFilter(input, List.of("a", "an"), true);
    assertEquals(3, output.size());
  }

  @Test
  void loadFromFileTest() throws Exception {
    List<String> lines =
        TextUtils.loadFromFile(Path.of(getClass().getResource("/test-input.txt").toURI()).toString());
    assertEquals(2, lines.size());
    assertEquals("Quick brown Fox jumped over a fat lazy Dog", lines.get(0));
  }

}
