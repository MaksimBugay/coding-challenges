package bmv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridSearch {

  public static String gridSearch(List<String> grid, List<String> pattern) {
    for (int i = 0; i < grid.size() - pattern.size() + 1; i++) {
      List<Set<Integer>> sets = new ArrayList<>();
      for (int j = 0; j < pattern.size(); j++) {
        sets.add(getAllPositions(grid.get(i + j), pattern.get(j)));
      }
      if (hasCommonValue(sets)) {
        return "YES";
      }
    }
    return "NO";
  }

  public static String gridSearch1(List<String> grid, List<String> pattern) {
    for (int i = 0; i < grid.size() - pattern.size() + 1; i++) {
      int posI = grid.get(i).indexOf(pattern.get(0));
      while (posI > -1) {
        for (int j = 1; j < pattern.size(); j++) {
          int posJ = grid.get(i + j).indexOf(pattern.get(j), posI);
          if (posI != posJ) {
            break;
          }
          if (j == pattern.size() - 1) {
            return "YES";
          }
        }
        posI = grid.get(i).indexOf(pattern.get(0), posI + 1);
      }
    }
    return "NO";
  }

  private static Set<Integer> getAllPositions(String s, String p) {
    return IntStream
        .iterate(s.indexOf(p), index -> index >= 0, index -> s.indexOf(p, index + 1))
        .boxed()
        .collect(Collectors.toSet());
  }

  private static boolean hasCommonValue(List<Set<Integer>> sets) {
    if (sets.size() == 0) {
      return false;
    }
    Set<Integer> reduced =
        sets.stream().reduce((first, second) -> {
          first.retainAll(second);
          return first;
        }).orElseThrow();
    return reduced.size() > 0;
  }
}
