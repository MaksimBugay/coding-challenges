package bmv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeapTest {

  @Test
  void basic(){
    Set<Integer> set = ConcurrentHashMap.newKeySet();
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

    minHeap.add(5);
    minHeap.add(7);
    minHeap.add(1);
    minHeap.add(1);
    minHeap.add(10);

    Assertions.assertEquals(1, minHeap.peek());
    Integer min = minHeap.poll();
    Assertions.assertEquals(1, min);
    min = minHeap.poll();
    Assertions.assertEquals(1, min);
    Assertions.assertEquals(5, minHeap.peek());
  }
}
