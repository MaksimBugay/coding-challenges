# Bead Ornaments

- Source: HackerRank
- Canonical URL: https://www.hackerrank.com/challenges/beadornaments/problem
- Captured page: https://www.hackerrank.com/challenges/beadornaments/problem?isFullScreen=true
- Problem slug: `beadornaments`
- Selected language: Java release 15, HackerRank label **Java 15**
- Language resolution source: **user**

## Problem statement

There are $N$ colors of beads. You have $b_i$ beads of the $i^{th}$ color. You
want to make an ornament by joining all the beads together. Use this algorithm:

1. Arrange all beads in any order so beads of the same color are together.
2. The ornament initially contains only the first bead in the arrangement.
3. For each later bead, join it to a bead of the same color already in the
   ornament. If the ornament has no bead of that color, join it to any bead in
   the ornament.

Every bead is distinct, including beads of the same color. Two ornaments differ
when some pair of beads is joined by a thread in one configuration and is not
joined in the other. Count the different ornaments this algorithm can form and
return the count modulo $10^9 + 7$.

**Clarification from HackerRank:** Treat the bead formation as a tree rather
than a straight line. Any number of beads may connect to one bead.

### Statement examples

For $b = [2]$, call the two beads $a1$ and $a2$. The orders $a1,a2$ and
$a2,a1$ produce the same single edge, so there is 1 ornament.

For $b = [2,2]$, call the beads $a1,a2,b1,b2$. The page lists these eight
group orders:

```text
1 A1, B1 = a1, a2, b1, b2
2 A2, B1 = a2, a1, b1, b2
3 A1, B2 = a1, a2, b2, b1
4 A2, B2 = a2, a1, b2, b1
5 B1, A1 = b1, b2, a1, a2
6 B2, A1 = b2, b1, a1, a2
7 B1, A2 = b1, b2, a2, a1
8 B2, A2 = b2, b1, a2, a1
```

Lines 1 and 8 have the same edges: $(a1,a2)$, $(a2,b1)$, and $(b1,b2)$.
The other equivalent pairs are $(2,6)$, $(3,7)$, and $(4,5)$, leaving 4
different ornaments.

## Function description

Complete `beadOrnaments`:

```java
public static int beadOrnaments(List<Integer> b)
```

`b[i]` is the number of beads of color `i`. Return the number of distinct
ornament trees modulo $1,000,000,007$.

## Input format

The first line contains the number of test cases $T$.

Each test case is a pair of lines:

1. An integer $n$, the number of elements in $b$.
2. $n$ space separated integers comprising $b$.

## Constraints

- $1 \le T \le 20$
- $1 \le n \le 10$
- $1 \le b[i] \le 30$

## Sample

### Input

```text
5
2
2 1
2
2 2
1
4
2
3 1
5
1 1 1 1 1
```

### Output

```text
2
4
16
9
125
```

### Explanation

- For `[2,1]`, four bead orders initially appear possible. They produce six
  construction paths but only two unique edge sets: `A1-A2-B1` and
  `A2-A1-B1`.
- For `[2,2]`, the four unique ornaments are the four paths described in the
  statement example.
- For `[4]`, the labeled trees are 12 paths and 4 stars, totaling 16.
- For five one-bead colors, treating the result as a tree gives 125 ornaments.

The page gives no separate output-format text beyond the function return
contract.

## Exact Java 15 starter code

```java
import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class Result {

    /*
     * Complete the 'beadOrnaments' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY b as parameter.
     */

    public static int beadOrnaments(List<Integer> b) {
    // Write your code here

    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int t = Integer.parseInt(bufferedReader.readLine().trim());

        IntStream.range(0, t).forEach(tItr -> {
            try {
                int bCount = Integer.parseInt(bufferedReader.readLine().trim());

                List<Integer> b = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                    .map(Integer::parseInt)
                    .collect(toList());

                int result = Result.beadOrnaments(b);

                bufferedWriter.write(String.valueOf(result));
                bufferedWriter.newLine();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        bufferedReader.close();
        bufferedWriter.close();
    }
}
```

## Analysis

For one color with $m$ beads, every possible ornament is a labeled tree on the
$m$ distinct beads. Cayley's formula gives $m^{m-2}$. The special case $m=1$
has one tree.

For multiple colors, first choose an internal labeled tree for each color.
Contract each color tree to one vertex. An edge between color vertices `i` and
`j` can select any of the $b_i b_j$ endpoint pairs. The weighted form of
Cayley's formula sums these choices over all trees on the colors:

$$
\left(\sum_i b_i\right)^{n-2}\prod_i b_i.
$$

Multiplying by the internal-tree counts $\prod_i b_i^{b_i-2}$ gives:

$$
\left(\sum_i b_i\right)^{n-2}\prod_i b_i^{b_i-1}.
$$

All multiplication and exponentiation use modular arithmetic. Binary modular
exponentiation avoids constructing any ornament.

### Invariants and edge cases

- After processing a color, the accumulator equals the product of all processed
  $b_i^{b_i-1}$ factors modulo $10^9+7$.
- During binary exponentiation, `result * base^remainingExponent` represents the
  original power modulo the modulus.
- A one-color, one-bead input returns 1 and avoids a negative exponent.
- Counts are positive under the stated constraints; invalid-input behavior is
  unspecified by the challenge.

### Complexity

Let $n$ be the number of colors and $S=\sum b_i$ (at most 300). Binary powers
take $O(\log S)$ time each, so the total is $O(n\log S)$ time and $O(1)$
auxiliary space, excluding the input list.

## Source capture notes

The statement, formulas, examples, constraints, and starter code were checked
against the selected HackerRank page. Math formulas were recovered from the
page's source model because the accessibility tree omits rendered SVG glyph
text. No problem image or diagram carries additional contract information.
