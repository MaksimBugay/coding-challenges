package bmv.string_similarity;

/**
 * HackerRank "String Similarity" — see
 * {@code src/main/bmv/string_similarity/task.md} for the problem statement
 * and {@code ai/string_similarity/} for the design/testing/development
 * handoff documents (contract v1).
 */
public final class Result {

    private Result() {
    }

    /**
     * Sum, over every suffix of {@code s} (including {@code s} itself), of
     * the length of the longest common prefix shared with {@code s}.
     *
     * <p>Contract: see {@code ai/string_similarity/api-contract.md} (API-001).
     *
     * <p>Uses the Z-array (Z-function): {@code z[i]} is the length of the
     * longest common prefix between {@code s} and the suffix starting at
     * {@code i}. The suffix at index 0 is {@code s} itself, contributing
     * {@code s.length()}; each suffix at {@code i >= 1} contributes
     * {@code z[i]}. The Z-array is built in {@code O(n)} using the standard
     * window technique (maintaining the rightmost previously-matched prefix
     * window {@code [l, r]} to avoid re-comparing already-matched
     * characters).
     *
     * @param s non-null lowercase string, {@code 1 <= s.length() <= 100000}
     * @return the sum of similarities, as an {@code int} (accumulated
     *     internally as {@code long}; see requirements.md Q-001)
     */
    public static int stringSimilarity(String s) {
        int n = s.length();
        long total = n; // similarity of s with itself (suffix at index 0)

        int[] z = new int[n];
        int l = 0;
        int r = 0;
        for (int i = 1; i < n; i++) {
            int zi = 0;
            if (i < r) {
                zi = Math.min(r - i, z[i - l]);
            }
            while (i + zi < n && s.charAt(zi) == s.charAt(i + zi)) {
                zi++;
            }
            if (i + zi > r) {
                l = i;
                r = i + zi;
            }
            z[i] = zi;
            total += zi;
        }
        return (int) total;
    }
}
