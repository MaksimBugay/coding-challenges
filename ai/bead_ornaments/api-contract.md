# Bead Ornaments API contract

Version: `bead-ornaments/v1.0.0`

## API-001 — Calculate ornament count

```java
Result.beadOrnaments(List<Integer> b) -> int
```

### Preconditions

- `b` contains 1 through 10 integers.
- Every integer is between 1 and 30 inclusive.
- The caller does not mutate the list during the call.

### Postconditions

- Returns the distinct constructible ornament-tree count modulo `1_000_000_007`.
- Returns a value from `0` through `1_000_000_006`.
- Does not modify `b` and performs no externally visible side effects.

Null, empty, malformed, and out-of-range inputs have unspecified behavior.

### Examples

| Input | Result | Reason |
| --- | ---: | --- |
| `[1]` | `1` | the single labeled vertex |
| `[2]` | `1` | one edge between two labeled beads |
| `[4]` | `16` | Cayley's `4^(4-2)` |
| `[2, 1]` | `2` | sample |
| `[1, 1, 1, 1, 1]` | `125` | Cayley's `5^(5-2)` |

The operation is deterministic, stateless, and safe for concurrent calls subject
to the caller-owned input precondition.
