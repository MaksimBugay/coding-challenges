package bmv.bead_ornaments;

import java.util.List;

/** HackerRank function boundary for the Bead Ornaments exercise. */
public final class Result {
    private static final long MODULUS = 1_000_000_007L;

    private Result() {
    }

    public static int beadOrnaments(List<Integer> b) {
        if (b.size() == 1) {
            int beadCount = b.get(0);
            return beadCount == 1 ? 1 : (int) modularPower(beadCount, beadCount - 2);
        }

        long totalBeads = 0;
        for (int beadCount : b) {
            totalBeads += beadCount;
        }

        long ornaments = modularPower(totalBeads, b.size() - 2);
        for (int beadCount : b) {
            ornaments = ornaments * modularPower(beadCount, beadCount - 1) % MODULUS;
        }
        return (int) ornaments;
    }

    private static long modularPower(long base, int exponent) {
        long result = 1;
        base %= MODULUS;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = result * base % MODULUS;
            }
            base = base * base % MODULUS;
            exponent >>= 1;
        }
        return result;
    }
}
