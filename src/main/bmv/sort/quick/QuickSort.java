package bmv.sort.quick;

import java.util.Arrays;

public class QuickSort {

  private final long[] source;

  public QuickSort(long[] source) {
    this.source = Arrays.copyOf(source, source.length);
  }

  public long[] sort() {
    quickSort(source);
    return Arrays.copyOf(source, source.length);
  }

  private void quickSort(long[] arr) {
    quickSort(arr, 0, arr.length - 1);
  }

  private void quickSort(long[] arr, int begin, int end) {
    if (begin < end) {
      int partitionIndex = partition(arr, begin, end);

      quickSort(arr, begin, partitionIndex - 1);
      quickSort(arr, partitionIndex + 1, end);
    }
  }

  private int partition(long[] arr, int begin, int end) {
    long pivot = arr[end];
    int i = begin - 1;

    for (int j = begin; j < end; j++) {
      if (arr[j] < pivot) {
        swap(arr, ++i, j);
      }
    }

    swap(arr, i + 1, end);

    return i + 1;
  }

  private void swap(long[] arr, int i, int j) {
    long swapTemp = arr[i];
    arr[i] = arr[j];
    arr[j] = swapTemp;
  }
}
