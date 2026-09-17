package bmv.insertion_sort_advanced_analysis;

import java.util.List;

public final class Result {
    private Result() {
    }

    public static int insertionSort(List<Integer> arr) {
        int[] values = new int[arr.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = arr.get(i);
        }

        return (int) countInversions(values, new int[values.length], 0, values.length);
    }

    private static long countInversions(int[] values, int[] buffer, int from, int to) {
        if (to - from < 2) {
            return 0;
        }

        int middle = (from + to) >>> 1;
        long inversions = countInversions(values, buffer, from, middle)
                + countInversions(values, buffer, middle, to);

        if (values[middle - 1] <= values[middle]) {
            return inversions;
        }

        int left = from;
        int right = middle;
        int destination = from;

        while (left < middle && right < to) {
            if (values[left] <= values[right]) {
                buffer[destination++] = values[left++];
            } else {
                buffer[destination++] = values[right++];
                inversions += middle - left;
            }
        }

        while (left < middle) {
            buffer[destination++] = values[left++];
        }
        while (right < to) {
            buffer[destination++] = values[right++];
        }

        System.arraycopy(buffer, from, values, from, to - from);
        return inversions;
    }
}
