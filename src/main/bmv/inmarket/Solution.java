package bmv.inmarket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class Solution {

  public static void main(String[] args) {
    String[][] boardingPasses = new String[][] {
        {"SYD", "LAX"},
        {"LHR", "DEL"},
        {"SFO", "JFK"},
        {"DEL", "PEK"},
        {"JFK", "LHR"},
        {"PEK", "SYD"},
    };

    List<String> itinerary = getItineraryOptimized(boardingPasses);

    for (String string : itinerary) {
      System.out.println(string);
    }
  }

  public static List<String> getItineraryOptimized(String[][] boardingPasses) {
    Map<String, String> fromToMap = new HashMap<>();
    Map<String, String> toFromMap = new HashMap<>();

    // Step 1: Build maps
    for (String[] pass : boardingPasses) {
      String from = pass[0];
      String to = pass[1];
      fromToMap.put(from, to);
      toFromMap.put(to, from);
    }

    // Step 2: Find the starting point
    String start = null;
    for (String from : fromToMap.keySet()) {
      if (!toFromMap.containsKey(from)) {
        start = from;
        break;
      }
    }

    // Step 3: Construct the itinerary
    List<String> itinerary = new ArrayList<>();
    while (start != null) {
      itinerary.add(start);
      start = fromToMap.get(start);
    }

    return itinerary;
  }

  public static List<String> getItinerary(String[][] boardingPasses) {
    LinkedList<String> itinerary = new LinkedList<>();

    Map<String, String> routs = Arrays.stream(boardingPasses).collect(Collectors.toMap(
        item -> item[0], item -> item[1]
    ));

    for (String[] boardingPass : boardingPasses) {
      String from = boardingPass[0];
      String to = boardingPass[1];
      if (routs.get(to) == null) {
        itinerary.add(to);
        itinerary.addFirst(from);
        break;
      }
    }

    String next = findByValue(itinerary.getFirst(), routs);
    while (next != null) {
      itinerary.addFirst(next);
      next = findByValue(next, routs);
    }

    return itinerary;
  }

  private static String findByValue(String value, Map<String, String> routs) {
    return routs.entrySet().stream()
        .filter(entry -> entry.getValue().equals(value))
        .map(Entry::getKey).findFirst().orElse(null);
  }
}
