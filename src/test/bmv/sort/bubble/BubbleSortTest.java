package bmv.sort.bubble;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class BubbleSortTest {

  @Test
  void sortsMixedValues() {
    assertArrayEquals(
        new long[] {Long.MIN_VALUE, -3, 0, 2, 2, Long.MAX_VALUE},
        new BubbleSort(new long[] {2, Long.MAX_VALUE, -3, 2, Long.MIN_VALUE, 0}).sort());
  }

  @Test
  void handlesEmptySingletonOrderedAndEqualArrays() {
    long[][] inputs = {{}, {7}, {1, 2, 3, 4}, {4, 3, 2, 1}, {5, 5, 5}};
    for (long[] input : inputs) {
      long[] expected = input.clone();
      Arrays.sort(expected);
      assertArrayEquals(expected, new BubbleSort(input).sort());
    }
  }

  @Test
  void doesNotModifyInput() {
    long[] input = {3, 1, 2};
    new BubbleSort(input).sort();
    assertArrayEquals(new long[] {3, 1, 2}, input);
  }

  @Test
  void isolatesInputAndReturnedArraysAcrossRepeatedCalls() {
    long[] input = {3, 1, 2};
    BubbleSort sorter = new BubbleSort(input);
    input[0] = -100;

    long[] result = sorter.sort();
    assertArrayEquals(new long[] {1, 2, 3}, result);
    result[0] = 100;
    assertArrayEquals(new long[] {1, 2, 3}, sorter.sort());
  }

  @Test
  void rejectsNullInput() {
    assertThrows(NullPointerException.class, () -> new BubbleSort(null));
  }
}
