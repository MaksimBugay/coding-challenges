package bmv.util;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class StreamUtils {

  private StreamUtils() {
  }

  public static List<Long> sortList(List<Long> list) {
    return list.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
  }

  public static Long getMaxElement(List<Long> list) {
    return list.stream().max(Comparator.naturalOrder()).orElse(null);
  }

  public static <T> T getLastElement(List<T> list) {
    return list.stream().reduce((first, second) -> second).orElseThrow();
  }

  public static Long getSumElements(List<Long> list) {
    return list.stream().reduce(Long::sum).orElse(0L);
  }

  public static <T> Set<T> getIntersection(List<Set<T>> sources) {
    return sources.stream().reduce((first, second) -> {
      first.retainAll(second);
      return first;
    }).orElse(Set.of());
  }
}
