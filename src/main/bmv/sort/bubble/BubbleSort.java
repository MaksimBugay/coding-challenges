package bmv.sort.bubble;

import java.util.Arrays;

/** Sorts longs in ascending order. Instances are intended for sequential use. */
public class BubbleSort {

  private final long[] source;

  public BubbleSort(long[] source) {
    this.source = Arrays.copyOf(source, source.length);
  }

  public long[] sort() {
    for (int end = source.length - 1; end > 0; end--) {
      boolean swapped = false;
      for (int i = 0; i < end; i++) {
        if (source[i] > source[i + 1]) {
          long temp = source[i];
          source[i] = source[i + 1];
          source[i + 1] = temp;
          swapped = true;
        }
      }
      if (!swapped) {
        break;
      }
    }
    return Arrays.copyOf(source, source.length);
  }
}
