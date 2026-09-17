# API Contract — String Similarity

Contract version: **v1**

## API-001: `Result.stringSimilarity`

```java
package bmv.string_similarity;

public final class Result {
    public static int stringSimilarity(String s);
}
```

- **Input**: `s` — non-null string of length `1..100000`, characters in
  `[a-z]`.
- **Output**: `int` — sum over every suffix of `s` (including `s` itself)
  of the length of the longest common prefix shared with `s`.
- **Errors**: no checked/unchecked exceptions are part of the contract for
  in-domain input. Out-of-domain input (null, empty, non-lowercase) is
  outside the stated constraints; behavior is unspecified and not
  contractually guaranteed (see architecture.md IF-001 preconditions).

### Examples

| Input `s` | Output | Basis |
| --- | --- | --- |
| `"ababaa"` | `11` | task.md Sample Input/Output case 1 |
| `"aa"` | `3` | task.md Sample Input/Output case 2 |
| `"a"` | `1` | boundary: single character |
| `"aaaa"` | `10` | boundary: all identical characters (4+3+2+1) |
| `"abcde"` | `5` | boundary: no shared prefixes among proper suffixes (5+0+0+0+0) |

### Failure/edge examples (documented, not defensively validated)

- `s = null` — outside contract; not asserted by tests (would throw
  `NullPointerException` naturally if reached, but this is incidental, not
  a guaranteed contract).
- `s.length() > 100000` or `s.length() == 0` — outside stated constraints;
  no test asserts a specific behavior.
