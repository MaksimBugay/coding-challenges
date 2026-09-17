package bmv.textstat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TextUtils {

  private static final String DEFAULT_WORD_DELIMITER = " ";

  private TextUtils() {
  }

  public static String[] getWords(String line, String delimiter) {
    String preparedLine = removeExtraSpaces(line);
    return preparedLine.split(Optional.ofNullable(delimiter).orElse(DEFAULT_WORD_DELIMITER));
  }

  public static String[] getWords(String line) {
    return getWords(line, null);
  }

  public static boolean isStartedWithCapitalLetter(String word) {
    return Character.isUpperCase(word.codePointAt(0));
  }

  public static List<String> transformAndFilter(List<String> input, List<String> excludeList,
      boolean capital) {
    boolean skipExclude = excludeList == null || excludeList.isEmpty();
    return input.stream()
        .filter(s -> skipExclude || !excludeList.contains(s))
        .filter(s -> !capital || isStartedWithCapitalLetter(s))
        .collect(Collectors.toList());
  }

  public static List<String> loadFromFile(String filepath) {
    try {
      return Files.lines(Paths.get(filepath), StandardCharsets.UTF_8).collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String removeExtraSpaces(String line) {
    return line.trim().replaceAll(" +", " ");
  }
}
