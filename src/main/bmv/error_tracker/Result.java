package bmv.error_tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {

  public static List<String> getErrorCodes(
      int k, int t, List<Integer> timestamps, List<String> errorCodes) {
    int lastIndex = timestamps.size() - 1;
    long windowStart = (long) timestamps.get(lastIndex) - t + 1;

    Map<String, Integer> frequencies = new HashMap<>();
    for (int index = lastIndex; index >= 0 && timestamps.get(index) >= windowStart; index--) {
      frequencies.merge(errorCodes.get(index), 1, Integer::sum);
    }

    List<String> result = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
      if (entry.getValue() >= k) {
        result.add(entry.getKey());
      }
    }
    result.sort(null);
    return result;
  }
}
