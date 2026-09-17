package bmv.longest_increasing_subsequence;

import java.util.List;

public final class Result {
    private Result() {
    }

    public static int longestIncreasingSubsequence(List<Integer> arr) {
        int[] tails = new int[arr.size()];
        int length = 0;

        for (int value : arr) {
            int position = lowerBound(tails, length, value);
            tails[position] = value;
            if (position == length) {
                length++;
            }
        }

        return length;
    }

    /**
     * Returns the leftmost index in {@code tails[0, length)} whose value is
     * {@code >= target}, or {@code length} if none exists. {@code tails} is
     * kept strictly ascending, so a plain binary search applies.
     */
    private static int lowerBound(int[] tails, int length, int target) {
        int low = 0;
        int high = length;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (tails[middle] < target) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        return low;
    }
}
