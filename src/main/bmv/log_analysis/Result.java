package bmv.log_analysis;

import java.util.ArrayList;
import java.util.List;

public class Result {

  public static List<Integer> getStaleServerCount(
      int n, List<List<Integer>> logData, List<Integer> query, int x) {
    List<Integer> result = new ArrayList<>(query.size());
    int[] seenGeneration = new int[n + 1];
    int generation = 0;

    for (int queryTime : query) {
      generation++;
      long windowStart = (long) queryTime - x;
      int activeServers = 0;

      for (List<Integer> logEntry : logData) {
        int requestTime = logEntry.get(1);
        if (requestTime < windowStart || requestTime > queryTime) {
          continue;
        }

        int serverId = logEntry.get(0);
        if (seenGeneration[serverId] != generation) {
          seenGeneration[serverId] = generation;
          activeServers++;
        }
      }

      result.add(n - activeServers);
    }

    return result;
  }
}
