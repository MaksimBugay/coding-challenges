# Solve Me First

- HackerRank page: https://www.hackerrank.com/challenges/solve-me-first/problem
- Captured tab URL: https://www.hackerrank.com/challenges/solve-me-first/problem?isFullScreen=true
- Captured title: `Solve Me First | HackerRank`

## Problem statement

Complete the `solveMeFirst` function to compute the sum of two integers.

### Example

For `a = 7` and `b = 3`, return `10`.

### Function description

Complete `solveMeFirst` with these parameters:

- `int a`: the first value
- `int b`: the second value

It returns an `int`: the sum of `a` and `b`.

### Input format

The supplied Java starter reads two whitespace-separated integers from standard input, one with each `Scanner.nextInt()` call.

### Output format

The supplied Java starter prints the integer returned by `solveMeFirst` followed by a line terminator.

### Constraints

`1 <= a, b <= 1000`

### Sample input

```text
a = 2
b = 3
```

### Sample output

```text
5
```

### Explanation

`2 + 3 = 5`.

No problem images or diagrams are needed to understand this contract.

## HackerRank Java environment

- Selected label: `Java 15`
- Java release: 15
- Resolution source: parsed directly from the page's visible language control

Exact starter code loaded for Java 15:

```java
import java.util.*;

public class Solution {

    static int solveMeFirst(int a, int b) {
      // Hint: Type return a+b; below 
    }

  
   public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int a;
        a = in.nextInt();
        int b;
        b = in.nextInt();
        in.close();
        int sum;
        sum = solveMeFirst(a, b);
        System.out.println(sum);
    }
}
```

## Required observable contract

- Required HackerRank function: `static int solveMeFirst(int a, int b)` in `Solution`.
- For every valid pair, return the mathematical sum of the two inputs.
- The supplied `main` reads the two integers and prints the returned sum.
- Inputs outside the stated constraints have no source-defined behavior.

## Algorithm analysis

Return `a + b`. The invariant is that the result equals the sum of the two supplied integers; there is no collection or mutable state to maintain. The valid minimum and maximum pairs are handled by the same expression. The maximum result is `2000`, which is safely within Java's `int` range.

Time complexity is `O(1)` and auxiliary-space complexity is `O(1)`.

## Capture notes

The page rendered identifiers and formulas as SVG MathJax, so plain accessibility text omitted them. The rendered formula shapes, visible starter code, sample values, and function signature were cross-checked together. No source material remained unreadable or conflicting.
