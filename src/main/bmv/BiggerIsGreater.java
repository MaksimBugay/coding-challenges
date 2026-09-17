package bmv;

import java.util.TreeMap;
import java.util.stream.Collectors;

public class BiggerIsGreater {

  private final TreeMap<Character, Integer> letters = new TreeMap<>();

  private final String input;

  public BiggerIsGreater(String input) {
    this.input = input;
    for (char c : input.toCharArray()) {
      Integer n = letters.putIfAbsent(c, 1);
      if (n != null) {
        letters.put(c, n + 1);
      }
    }
  }

  public String biggerIsGreater() {
    Integer[] swapCandidates = getSwapCandidates();

    if (swapCandidates == null) {
      return "no answer";
    }
    String tmp = swap(swapCandidates);

    BiggerIsGreater biggerIsGreater = new BiggerIsGreater(tmp.substring(swapCandidates[1] + 1));

    return tmp.substring(0, swapCandidates[1] + 1) + biggerIsGreater.print();
  }

  private String swap(Integer[] swapCandidates) {
    char[] chars = input.toCharArray();
    char buf = chars[swapCandidates[1]];
    chars[swapCandidates[1]] = chars[swapCandidates[0]];
    chars[swapCandidates[0]] = buf;

    return new String(chars);
  }

  public Integer[] getSwapCandidates() {
    char[] chars = input.toCharArray();

    Integer[] swapCandidates = new Integer[2];
    for (int i = chars.length - 2; i > -1; i--) {
      swapCandidates[1] = i;
      char v = chars[i];
      TreeMap<Character, Integer> buf = new TreeMap<>();
      for (int j = i + 1; j < chars.length; j++) {
        if (chars[j] > v) {
          buf.put(chars[j], j);
        }
      }
      if (buf.size() > 0) {
        swapCandidates[0] = buf.firstEntry().getValue();
        return swapCandidates;
      }
    }
    return null;
  }

  public TreeMap<Character, Integer> getLetters() {
    return letters;
  }

  public String print() {
    return letters.entrySet().stream()
        .map(entry -> String.valueOf(entry.getKey()).repeat(entry.getValue()))
        .collect(Collectors.joining());
  }
}