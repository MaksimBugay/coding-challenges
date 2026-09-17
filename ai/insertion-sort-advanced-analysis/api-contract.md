# API contract

## API-001 — `Result.insertionSort`, version 1

```java
public static int insertionSort(List<Integer> arr)
```

The valid domain is a non-null list of 1 to 100,000 non-null integers in `[1, 10000000]`. The operation returns the Java `int` narrowing of the number of pairs `(i, j)` satisfying `i < j` and `arr[i] > arr[j]`. It leaves the list unchanged. Behavior outside the valid domain is unspecified.

Examples:

| Input | Result |
| --- | ---: |
| `[1]` | `0` |
| `[1,1,1,2,2]` | `0` |
| `[2,1,3,1,2]` | `4` |
| `[4,3,2,1]` | `6` |
| `[2,2,1,1]` | `4` |

The operation is stateless. Concurrent calls with independently owned or concurrently unmodified lists share no production state. Mutation by another thread during a call is outside the contract.
