# Log Analysis API contract

## Contract version 1

This exercise exposes one synchronous Java operation. Requirements and valid-input limits are defined in [requirements.md](requirements.md).

## API-001 — Count stale servers for request windows

```java
List<Integer> result = bmv.log_analysis.Result.getStaleServerCount(
    n, logData, query, x);
```

The method is `public static`, accepts `(int, List<List<Integer>>, List<Integer>, int)` in the order shown, and returns `List<Integer>`. Its source must be compatible with Java 17.

### Input semantics

- `n` is the number of servers, identified by integers 1 through `n`.
- `logData` contains `m` non-null rows; row `j` is `[serverId, time]`.
- `query` contains `q` integer query times.
- `x` is the amount subtracted from each query time to form the inclusive lower boundary.
- Valid limits are those in REQ-004. Logs and queries may be unsorted, and repeated server IDs, timestamps, rows, and query values are permitted by the stated domain.

### Output semantics

For every query index `i`, define:

```text
start = query[i] - x
end   = query[i]
active = distinct server IDs from rows whose time is in [start, end]
result[i] = n - size(active)
```

The returned list is non-null, contains exactly `q` integers, and preserves query order. Each answer is in `[0,n]`.

### Concrete examples

| Description | `n` | `logData` | `query` | `x` | Result |
| --- | ---: | --- | --- | ---: | --- |
| Statement example | 3 | `[[3,3],[2,6],[1,5]]` | `[10,11]` | 5 | `[1,2]` |
| Sample Case 0 | 6 | `[[3,2],[4,3],[2,6],[6,3]]` | `[3,2,6]` | 2 | `[3,5,5]` |
| Both boundaries and outside values | 4 | `[[1,5],[2,10],[3,4],[4,11]]` | `[10]` | 5 | `[2]` |
| Repeated requests by one server | 3 | `[[1,4],[1,5],[1,5]]` | `[5]` | 1 | `[2]` |
| No request in window | 2 | `[[1,1]]` | `[10]` | 2 | `[2]` |
| Duplicate, unsorted queries | 2 | `[[1,3],[2,8]]` | `[8,3,8]` | 1 | `[1,1,1]` |

### Boundary and failure semantics

Valid invocations with no in-window request succeed with the value `n` for that query. Inputs outside the stated domain have no promised return value or exception. Callers must not depend on observed invalid-input behavior, validation order, returned-list mutability, or input mutation behavior.

The operation declares no I/O, persistence, external resource, collaborator, shared-state lifecycle, or ownership transfer. Contract version 1 does not specify a concurrency guarantee.

### Compatibility baseline

Version 1 freezes package `bmv.log_analysis`, public class `Result`, method name `getStaleServerCount`, `public static` access, parameter order and types `(int, List<List<Integer>>, List<Integer>, int)`, return type `List<Integer>`, and Java 17 source compatibility. The package and camel-case parameter spelling are repository adaptations of the screenshot's Java starter; parameter names are not bytecode API elements.
