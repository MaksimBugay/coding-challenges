# String Similarity

- HackerRank title: **String Similarity**
- Canonical page URL: https://www.hackerrank.com/challenges/string-similarity/problem

## Statement

For two strings A and B, we define the similarity of the strings to be the
length of the longest prefix common to both strings. For example, the
similarity of strings "abc" and "abd" is 2, while the similarity of strings
"aaa" and "aaab" is 3.

Calculate the sum of similarities of a string S with each of its suffixes.

## Input Format

The first line contains the number of test cases *t*. Each of the next *t*
lines contains a string to process, *s*.

## Constraints

- `1 ≤ t ≤ 10`
- `1 ≤ |s| ≤ 100000`
- `s` is composed of characters in the range `ascii[a-z]`

## Output Format

Output *t* lines, each containing the answer for the corresponding test case.

## Sample Input

```
2
ababaa
aa
```

## Sample Output

```
11
3
```

## Explanation

For the first case, the suffixes of the string are "ababaa", "babaa", "abaa",
"baa", "aa" and "a". The similarities of these strings with the string
"ababaa" are 6, 0, 3, 0, 1, & 1 respectively. Thus, the answer is
6 + 0 + 3 + 0 + 1 + 1 = 11.

For the second case, the answer is 2 + 1 = 3.

## Java version

- Selected HackerRank label: **Java 15**
- Resolution source: `user`

## Starter code (Java 15, as loaded on the challenge page)

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
     * Complete the 'stringSimilarity' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts STRING s as parameter.
     */

    public static int stringSimilarity(String s) {
        // Write your code here

    }

}
```

(The remainder of the starter template, main-method I/O scaffolding, is
standard HackerRank boilerplate and is not reproduced here; the required
production entry point is `Result.stringSimilarity(String s) -> int`.)

## Required function/class signature

- Class: `Result`
- Method: `public static int stringSimilarity(String s)`
- Input: a single lowercase string `s` (`ascii[a-z]`), `1 ≤ |s| ≤ 100000`.
- Output: an `int`, the sum over every suffix of `s` (including `s` itself) of
  the length of the longest common prefix between that suffix and `s`.
- The overall program reads `t` on the first line, then `t` lines of `s`, and
  prints one integer answer per line, in order.

## Algorithm analysis

This is the classic Z-function (Z-array) application. For a string `s` of
length `n`, define `z[i]` (for `i ≥ 1`) as the length of the longest common
prefix between `s` and the suffix of `s` starting at index `i`. By
definition `z[0]` is not part of the standard Z-array (or is defined as `n`,
depending on convention), and it corresponds to the similarity of `s` with
itself, which is `n`.

The answer is:

```
n + sum(z[i] for i in 1..n-1)
```

which is exactly the sum of similarities of `s` with all of its suffixes
(the suffix starting at 0 is `s` itself, contributing `n`; each suffix
starting at `i ≥ 1` contributes `z[i]`).

**Z-array construction (linear time):** maintain a window `[l, r]` that is
the interval with the largest `r` found so far such that `s[l..r]` is a
prefix of `s`. For each `i > l`:

- If `i < r`, initialize `z[i] = min(r - i, z[i - l])` (reuse previously
  computed information from the mirrored position `i - l` inside the
  window), then try to extend it by direct character comparison.
- If `i ≥ r`, compute `z[i]` from scratch by direct character comparison.
- After computing `z[i]`, if `i + z[i] > r`, update `l = i`, `r = i + z[i]`.

Each character comparison either extends `r` (bounded by `n`) or fails at
most once per index, so the total work is `O(n)`.

**Invariants:**
- `r` is non-decreasing across the loop.
- `z[i]` never exceeds `n - i` (cannot extend past the end of the string).

**Edge cases:**
- `|s| = 1`: answer is `1` (only the string itself, similarity `1`).
- All characters identical (e.g. "aaaa...a"): every suffix has maximal
  similarity, worst case for a naive `O(n^2)` approach but still `O(n)`
  here since each Z-array step does O(1) amortized work.
- Multiple test cases share no state; each string is processed
  independently.

**Complexity:**
- Time: `O(n)` per test case (Z-array is linear), so `O(sum(n_i))` overall,
  well within limits for `t ≤ 10` and `n ≤ 100000`.
- Auxiliary space: `O(n)` for the Z-array.

A naive approach comparing every suffix against `s` character-by-character
is `O(n^2)` per test case, which is `10^10` in the worst case (`n =
100000`) — too slow. The Z-array approach is required to meet the
constraints.

## Notes on captured material

No diagrams or images accompany this problem; all constraints were
transcribed directly from the rendered (MathJax) constraint list on the
problem page. No ambiguity or conflicting material was found in the source.
