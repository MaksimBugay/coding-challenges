# Recent Error Tracker API contract

## Contract version 1

This exercise exposes one synchronous Java operation. Requirements and valid-input limits are defined in [requirements.md](requirements.md).

## API-001 — Find recent frequent error codes

```java
List<String> result = bmv.error_tracker.Result.getErrorCodes(
    k, t, timestamps, errorCodes);
```

The method is `public static`, accepts `(int, int, List<Integer>, List<String>)` in the order shown, and returns `List<String>`. Its source must be compatible with Java 17.

### Input semantics

- `timestamps` and `errorCodes` are non-null lists of equal size `n`.
- `1 <= n, k <= 100,000`.
- Timestamps are integers in `[1, 1,000,000,000]` and are sorted in non-decreasing order.
- `1 <= t <= timestamps.get(n - 1)`.
- Each code is a non-null alphanumeric string of length 1 through 10.
- `timestamps.get(i)` and `errorCodes.get(i)` describe the same log entry.

### Output semantics

Set:

```text
last = timestamps.get(n - 1)
start = last - t + 1
```

Only entries with timestamps in the inclusive interval `[start, last]` contribute to counts. The returned list contains each distinct code with an in-window count greater than or equal to `k`, exactly once, in ascending Java `String` natural order. The result is non-null. When no code qualifies, it is an empty list.

### Concrete examples

| Description | `k` | `t` | Timestamps | Codes | Result |
| --- | ---: | ---: | --- | --- | --- |
| Problem example | 2 | 10 | `[100, 101, 102, 105, 110]` | `[E1, E2, E1, E1, E2]` | `[E1, E2]` |
| Sample Case 0 | 3 | 5 | `[1, 2, 4, 5, 6, 7, 10]` | `[E1, E2, E1, E1, E2, E2, E2]` | `[E2]` |
| Sample Case 1 | 2 | 4 | `[1, 2, 4, 5, 6]` | `[E1, E2, E1, E1, E2]` | `[E1]` |
| Inclusive lower boundary | 2 | 5 | `[5, 5, 6, 10]` | `[E2, E2, E1, E1]` | `[E1]` |
| No qualifying code | 2 | 2 | `[1, 2, 3]` | `[E1, E2, E3]` | `[]` |
| Duplicate latest timestamp | 1 | 1 | `[7, 8, 8]` | `[OLD, B, A]` | `[A, B]` |

For the inclusive-boundary example, the window is `[6, 10]`: both timestamp-5
`E2` entries are excluded, while the `E1` entries at 6 and 10 are included. A
lower-bound error would incorrectly add `E2` to the result.

### Boundary and failure semantics

A valid invocation with no qualifying code succeeds with an empty list; it does not throw. Inputs outside the stated domain have no promised return value or exception. Callers must not depend on observed invalid-input behavior, validation order, returned-list mutability, or input mutation behavior.

The operation declares no I/O, persistence, external resource, collaborator, shared-state lifecycle, locale transformation, or case folding. Contract version 1 does not specify a concurrency guarantee.

### Compatibility baseline

Version 1 freezes package `bmv.error_tracker`, public class `Result`, method name `getErrorCodes`, `public static` access, parameter order and types `(int, int, List<Integer>, List<String>)`, return type `List<String>`, and Java 17 source compatibility. The package is the repository-layout adaptation of the screenshot's otherwise unchanged Java starter boundary.
