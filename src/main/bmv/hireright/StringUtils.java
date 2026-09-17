package bmv.hireright;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  private static final Pattern SPLIT_TO_WORDS_PATTERN = Pattern.compile("(\\d)|([a-zA-Z]+)");

  private StringUtils() {
  }

  public static boolean isStartedWithCapitalLetter(String word) {
    String firstLetter = word.substring(0, 1);
    return firstLetter.equals(firstLetter.toUpperCase());
  }

  public static String[] splitIntoWords(String source) {
    return source.split("\\W+");
    /*List<String> words = new ArrayList<>();
    String tmp = source;
    while (!tmp.isEmpty()) {
      Optional<String> oWord = getFirstWord(source);
      if (oWord.isEmpty()) {
        break;
      } else {
        words.add(oWord.get());
        tmp = tmp.replace(oWord.get(), "");
      }
    }
    return words;*/
  }

  public static String[] excludeIgnored(String[] words, Set<String> exclusions) {
    return Arrays.stream(words)
        .filter(w -> !exclusions.contains(w))
        .toList().toArray(new String[0]);
  }
}
