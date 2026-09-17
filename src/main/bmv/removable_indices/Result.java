package bmv.removable_indices;

import java.util.ArrayList;
import java.util.List;

public class Result {

  public static List<Integer> getRemovableIndices(String str1, String str2) {
    int targetLength = str2.length();

    int latestRemovalIndex = 0;
    while (latestRemovalIndex < targetLength
        && str1.charAt(latestRemovalIndex) == str2.charAt(latestRemovalIndex)) {
      latestRemovalIndex++;
    }

    int earliestRemovalIndex = targetLength;
    while (earliestRemovalIndex > 0
        && str1.charAt(earliestRemovalIndex) == str2.charAt(earliestRemovalIndex - 1)) {
      earliestRemovalIndex--;
    }

    if (earliestRemovalIndex > latestRemovalIndex) {
      return List.of(-1);
    }

    List<Integer> removableIndices =
        new ArrayList<>(latestRemovalIndex - earliestRemovalIndex + 1);
    for (int index = earliestRemovalIndex; index <= latestRemovalIndex; index++) {
      removableIndices.add(index);
    }
    return removableIndices;
  }
}
