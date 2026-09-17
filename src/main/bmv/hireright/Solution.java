package bmv.hireright;

import static bmv.hireright.StringUtils.excludeIgnored;
import static bmv.hireright.StringUtils.isStartedWithCapitalLetter;
import static bmv.hireright.StringUtils.splitIntoWords;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

class Solution {

  public static void main(String[] args) {

    //String filePath = args[0];

    String filePath = "text-stat-test.txt";

    String source = loadFile(filePath);

    printTextStatistic(source);
  }

  private static String loadFile(String filepath) {
    try {
      return Files.readString(Path.of(filepath), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void printTextStatistic(String source) {

    String[] words = splitIntoWords(source);

    System.out.println(Arrays.toString(words));

    System.out.println(isStartedWithCapitalLetter(words[0]));

    System.out.println(isStartedWithCapitalLetter(words[2]));

    Set<String> exclusions = Set.of("a", "an", "the");
    String[] afterExclusion = excludeIgnored(words, exclusions);

    System.out.println(Arrays.toString(afterExclusion));

    TextStatisticCalculator calculator = new TextStatisticCalculator(source, exclusions);

    System.out.println(calculator.printStatistic());
  }
}
