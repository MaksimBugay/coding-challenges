package bmv.sort.merge;

import java.util.Arrays;

public class MergeSort {

  private final long[] source;

  public MergeSort(long[] source) {
    this.source = Arrays.copyOf(source, source.length);
  }

  public long[] sort() {
    mergeSort(source, source.length);
    return Arrays.copyOf(source, source.length);
  }

  private void mergeSort(long[] arr, int n) {
    if (n < 2) {
      return;
    }
    int mid = n / 2;
    long[] l = Arrays.copyOfRange(arr, 0, mid);
    long[] r = Arrays.copyOfRange(arr, mid, n);

    mergeSort(l, mid);
    mergeSort(r, n - mid);

    merge(arr, l, r, mid, n - mid);
  }

  private void merge(
      long[] a, long[] l, long[] r, int left, int right) {

    int i = 0, j = 0, k = 0;
    while (i < left && j < right) {
      if (l[i] <= r[j]) {
        a[k++] = l[i++];
      } else {
        a[k++] = r[j++];
      }
    }
    while (i < left) {
      a[k++] = l[i++];
    }
    while (j < right) {
      a[k++] = r[j++];
    }
  }

}
