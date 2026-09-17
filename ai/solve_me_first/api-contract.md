# Solve Me First API contract

## Contract version 1

### API-001 — Add two valid integers

```java
int result = SolveMeFirst.solveMeFirst(a, b);
```

Precondition: `1 <= a <= 1000` and `1 <= b <= 1000`.

Postcondition: `result` equals the mathematical value `a + b`. The call completes normally for every valid input and has no observable side effect.

| Input | Result |
| --- | --- |
| `(2, 3)` | `5` |
| `(7, 3)` | `10` |
| `(1, 1)` | `2` |
| `(1000, 1000)` | `2000` |

Behavior outside the valid input domain is unspecified. Contract version 1 defines no validation order or exception behavior.

### API-002 — Standalone HackerRank program

`Solution.main` reads two whitespace-separated integers, calls `solveMeFirst`, and prints the returned integer with a line terminator. The required HackerRank method is `static int solveMeFirst(int a, int b)`.
