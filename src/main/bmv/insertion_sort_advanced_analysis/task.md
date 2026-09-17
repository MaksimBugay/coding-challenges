# Insertion Sort Advanced Analysis

- Source: https://www.hackerrank.com/challenges/insertion-sort/problem?isFullScreen=true
- HackerRank slug: `insertion-sort`
- Captured language: **Java 15**
- Java resolution source: the page's selected language label, parsed directly from the open challenge

## Problem statement

Insertion Sort is a simple sorting technique covered in earlier challenges. For large arrays, running insertion sort itself is too slow. Calculate how many element shifts insertion sort would perform while sorting the array.

If `k[i]` is the number of elements over which the `i`th array element shifts, the total is `k[1] + k[2] + ... + k[n]`.

For `arr = [4, 3, 2, 1]`:

```text
Array          Shifts
[4,3,2,1]
[3,4,2,1]     1
[2,3,4,1]     2
[1,2,3,4]     3

Total shifts = 1 + 2 + 3 = 6
```

## Function description

Complete `insertionSort`:

```java
public static int insertionSort(List<Integer> arr)
```

The input is an integer array and the result is the number of shifts required to sort it. For valid inputs the method has no specified side effects; this package treats the input list as caller-owned and does not mutate it.

## Input format

The first line contains `t`, the number of queries. Each query contains two lines:

1. `n`, the length of `arr`.
2. `n` space-separated integers `arr[i]`.

## Output format

The page leaves its Output Format section blank. The supplied Java 15 driver writes one decimal result per query, followed by a newline.

## Constraints

- `1 <= t <= 15`
- `1 <= n <= 100000`
- `1 <= arr[i] <= 10000000`

## Sample

Input:

```text
2
5
1 1 1 2 2
5
2 1 3 1 2
```

Output:

```text
0
4
```

The first query is already sorted. The second progresses as follows:

```text
Array: 2 1 3 1 2 -> 1 2 3 1 2 -> 1 1 2 3 2 -> 1 1 2 2 3
Moves:   -        1       -    2         -  1            = 4
```

## Source ambiguity

The page requires an `int` result, and the Java 15 starter stores and prints an `int`. At the stated maximum `n`, the mathematical inversion count can exceed `Integer.MAX_VALUE`. The exact HackerRank starter contract is authoritative here: the implementation returns Java's low 32 bits after normal `int` narrowing. The algorithm keeps a `long` internal count so intermediate additions are well-defined before the final narrowing conversion.

No meaningful statement image or diagram is present; the array walkthrough is text and is preserved above.

## Algorithm analysis

The shift count equals the number of inversions: pairs `(i, j)` with `i < j` and `arr[i] > arr[j]`. A modified merge sort recursively counts inversions in each half, then counts cross-half inversions during the merge. When the next right value is smaller than the next left value, it precedes every remaining value in the left half, contributing `leftEnd - leftIndex` inversions.

The merge invariant is that the already-written portion is sorted and the running total equals all inversions completely contained in that written portion plus all inversions returned by the two recursive calls. Equal values are taken from the left half first and therefore do not count as inversions. Singleton arrays return zero. Already sorted arrays return zero; reverse-sorted arrays return `n(n-1)/2` before the starter-required narrowing.

For `n` elements, time is `O(n log n)` and auxiliary space is `O(n)` for the copied values and merge buffer, plus `O(log n)` recursion stack. The method does not mutate the caller's list.

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
     * Complete the 'insertionSort' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY arr as parameter.
     */

    public static int insertionSort(List<Integer> arr) {
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
                int n = Integer.parseInt(bufferedReader.readLine().trim());

                List<Integer> arr = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                    .map(Integer::parseInt)
                    .collect(toList());

                int result = Result.insertionSort(arr);

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
