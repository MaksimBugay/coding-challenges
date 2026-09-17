package bmv.hireright;

import static bmv.hireright.StringUtils.excludeIgnored;
import static bmv.hireright.StringUtils.splitIntoWords;

import java.util.Set;

public class TextStatisticCalculator {

  private final String text;

  private final Set<String> exclusions;

  private TextStatistic textStatistic;

  public TextStatisticCalculator(String text, Set<String> exclusions) {
    this.text = text;
    this.exclusions = exclusions == null ? Set.of() : exclusions;
  }

  private void calculate() {
    if (text == null || text.isEmpty()) {
      this.textStatistic = new TextStatistic(0, 0);
      return;
    }

    String[] words = excludeIgnored(splitIntoWords(text), exclusions);

    this.textStatistic = new TextStatistic(words.length, text.length());
  }

  public String printStatistic() {
    if (textStatistic == null) {
      calculate();
    }
    return textStatistic.toString();
  }

  public record TextStatistic(int wordsTotal, int symbolsTotal) {

  }

}
