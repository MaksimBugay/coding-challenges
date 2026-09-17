package bmv;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SwapContainers {

  public static String organizingContainers(List<List<Integer>> containers) {
    Set<Integer> contCapacity =
        containers.stream().map(list -> list.stream().reduce(0, Integer::sum))
            .collect(Collectors.toSet());
    for (int i = 0; i < containers.size(); i++) {
      int index = i;
      int typeSum = containers.stream().map(list -> list.get(index)).reduce(0, Integer::sum);
      if (!contCapacity.contains(typeSum)) {
        return "Impossible";
      }
    }
    return "Possible";
  }

}
