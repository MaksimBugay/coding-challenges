package bmv;

import bmv.Queen.QueenStep;
import bmv.prefix.LongestCommonPrefix;
import bmv.sort.merge.MergeSort;
import bmv.sort.quick.QuickSort;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainBk {

  private static String findSequenceAndRemove(String word, int k) {
    char[] chars = word.toCharArray();

    int index = 0;

    while (index < chars.length - k + 1) {
      boolean fit = true;
      char first = chars[index];
      for (int i = index; i < index + k; i++) {
        if (chars[i] != first) {
          fit = false;
          break;
        }
      }
      if (fit) {
        return word.substring(index, index + k);
      }
      index = index + 1;
    }
    return word;
  }

  public static String compressWord(String word, int k) {
    // Write your code here
    String result = word;
    while (true) {
      String tmpResult = findSequenceAndRemove(result, k);
      if (result.equals(tmpResult)) {
        break;
      }
      result = tmpResult;
    }
    return result;
  }

  public static int queensAttack(int n, int k, int r_q, int c_q, List<List<Integer>> obstacles) {
    if (!(n > 0 && n <= Math.pow(10, 5))) {
      return 0;
    }
    if (!(k >= 0 && k <= Math.pow(10, 5))) {
      return 0;
    }
    // Write your code here
    final Point checkPoint = new Point(r_q, c_q);
    Queen queen = new Queen();

    Board board = new Board(n, obstacles, checkPoint);

    int[] counter = new int[] {0};

    for (QueenStep queenStep : QueenStep.values()) {
      queen.setCurrentPosition(checkPoint);
      while (board.isPositionAllowed(queen.doStep(queenStep))) {
        counter[0]++;
      }
    }

    return counter[0];
  }

  public static void main(String[] args) {
    System.out.println("Hello World!");
    List<String> filePaths = List.of(args[0].split(","));
    List<String> stopWords = List.of(args[1].split(","));
    boolean capital = "L".equals(args[2]);
    boolean withTotalCount = "C".equals(args[3]);

    
  }

  public static void mainOld(String[] args) {
    String[] strings = new String[] {"flower","flow","flight"};
    LongestCommonPrefix longestCommonPrefix = new LongestCommonPrefix();
    System.out.println(longestCommonPrefix.longestCommonPrefix(strings));
    if (true) return;
    System.out.println(
        GridSearch.gridSearch(
            List.of(
                 "111111111111111"
                ,"111111111111111"
                ,"111111011111111"
                ,"111111111111111"
                ,"111111111111111"),
            List.of(
                "11111"
                ,"11111"
                ,"11110"))
    );

    System.out.println(
        GridSearch
            .gridSearch(List.of("123412", "561212", "123634", "781288"), List.of("12", "34"))
    );

    System.out.println(Encryption.encrypt("haveaniceday"));

    BiggerIsGreater bIsg = new BiggerIsGreater(
        "ocsmerkgidvddsazqxjbqlrrxcotrnfvtnlutlfcafdlwiismslaytqdbvlmcpapfbmzxmftrkkqvkpflxpezzapllerxyzlcf");
    System.out.println(bIsg.biggerIsGreater());
    if (!"ocsmerkgidvddsazqxjbqlrrxcotrnfvtnlutlfcafdlwiismslaytqdbvlmcpapfbmzxmftrkkqvkpflxpezzapllerxyzlfc"
        .equals(bIsg.biggerIsGreater())) {
      throw new IllegalStateException();
    }
    String isPossible = SwapContainers.organizingContainers(
        List.of(List.of(999336263, 998799923), List.of(998799923, 999763019))
    );

    System.out.println(isPossible);
    Solution  solution = new Solution();
    String s = solution.solution("ACCAABBC");
    System.out.println(s);

    int n = queensAttack(5, 3, 4, 3, List.of(List.of(4, 2), List.of(2, 3), List.of(5, 5)));
    System.out.println(n);
    n = queensAttack(100000, 0, 4187, 5068, List.of());
    System.out.println(n);

    QuickSort quickSort = new QuickSort(new long[] {10, 80, 30, 90, 40, 60, 50, 11, 3});

    System.out.println(Arrays.toString(quickSort.sort()));

    MergeSort mergeSort = new MergeSort(new long[] {10, 80, 30, 90, 40, 60, 50, 11, 3});

    System.out.println(Arrays.toString(mergeSort.sort()));

    if (true) {
      return;
    }

    System.out.println("Start");

    Map<String, Integer> input = Map.of("1", 4, "2", 4, "3", 2);

    int sumLoad = input.values().stream().parallel().mapToInt(i -> i).sum();

    int evenLoad = sumLoad / input.size();

    AtomicInteger rem = new AtomicInteger(sumLoad % input.size());

    Map<String, Integer> result = input.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, entry -> {
          if (rem.getAndDecrement() > 0) {
            return evenLoad + 1;
          } else {
            return evenLoad;
          }
        }));
    System.out.println("Even load = " + evenLoad);
    System.out.println("Total load = " + evenLoad);
    System.out.println(result);

  }


  public static int solution(String s) {
    int n = 0;
    String buf = s;
    while (buf.length() > 0) {
      buf = cutWord(buf);
      n++;
    }
    return n;
  }

  public static String cutWord(String s) {
    if (s == null || s.length() == 0) {
      return s;
    }
    Set<Character> letters = new HashSet<>();

    char[] chars = s.toCharArray();

    int n = 0;
    while (n < chars.length) {
      if (!letters.contains(chars[n])) {
        letters.add(chars[n]);
        n++;
        continue;
      }
      break;
    }
    return s.substring(n);
  }

  static class SampleNormalizer {

    public Optional<Optional<BigDecimal>> normalize(BigDecimal value) {
      return Optional.of(Optional.ofNullable(value));
    }
  }

  static class SamplePreprocessor {

    private final SampleNormalizer normalizer;

    SamplePreprocessor(SampleNormalizer normalizer) {
      this.normalizer = normalizer;
    }

    Stream<BigDecimal> preprocess(Stream<BigDecimal> input) {
      if (input == null) {
        return Stream.empty();
      }
      /*Iterator<BigDecimal> iterator = input.iterator();
      if (!iterator.hasNext()) {
        return Stream.empty();
      }*/

      AtomicInteger counter = new AtomicInteger();
      input
          .filter(val -> val != null && val.compareTo(BigDecimal.ZERO) > 0)
          .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 3))
          .values()
          .stream()
          .filter(val -> val.size() == 3)
          .filter(
              val -> val.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(Double.NaN)
                  < 30)
          .flatMap(Collection::stream)
          .map(val -> normalizer.normalize(val).orElse(Optional.empty()))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(System.out::print);

      return input;
    }
  }
}
