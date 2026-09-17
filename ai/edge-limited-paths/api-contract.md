# Edge-Limited Path Queries API contract

## Contract version 1

This task exposes one synchronous Java operation through `bmv.edge_limited_paths.EdgeLimitedPaths`.

## API-001 — Determine edge-limited connectivity

```java
EdgeLimitedPaths operation = new EdgeLimitedPaths();
boolean[] result = operation.distanceLimitedPathsExist(n, edgeList, queries);
```

The method is public and has the exact parameter and return types shown above. It has no collaborator, I/O, or external resource.

### Input semantics

- `n` numbers vertices from `0` through `n - 1`.
- Every `edgeList` row is `[from, to, weight]` and describes an undirected edge.
- Multiple edge rows may connect the same vertices.
- Every `queries` row is `[start, end, limit]`.
- All values satisfy the ranges and structural constraints in REQ-004.

### Output semantics

The result is non-null and has `queries.length` elements. For query index `i`, `result[i]` is true exactly when some path connects its endpoints and every edge on that path has weight strictly less than its limit. The result index always corresponds to the original query index.

Invalid-input behavior, input mutation, and thread safety are unspecified. Callers must not depend on observed behavior outside the valid domain or on input-array preservation.

### Concrete examples

| Description | `n`, edges, queries | Result |
| --- | --- | --- |
| Required example 1 | `3`, `[[0,1,2],[1,2,4],[2,0,8],[1,0,16]]`, `[[0,1,2],[0,2,5]]` | `[false,true]` |
| Required example 2 | `5`, `[[0,1,10],[1,2,5],[2,3,9],[3,4,13]]`, `[[0,4,14],[1,4,13]]` | `[true,false]` |
| Undirected traversal | `3`, `[[0,1,3],[1,2,4]]`, `[[2,0,5]]` | `[true]` |
| Disconnected | `4`, `[[0,1,1],[2,3,1]]`, `[[0,3,2]]` | `[false]` |
| Parallel-edge boundary | `2`, `[[0,1,7],[0,1,3]]`, `[[0,1,4],[0,1,3]]` | `[true,false]` |
| Original query order | `3`, `[[0,1,2],[1,2,4]]`, `[[0,2,5],[0,2,4],[0,1,3]]` | `[true,false,true]` |
| Numeric limits | `2`, `[[0,1,1000000000]]`, `[[0,1,1000000000]]` | `[false]` |

### Success and contractual failure examples

A valid disconnected query is a successful invocation returning `false`; it is not an error. A weight equal to the limit is also a normal `false` result. A null matrix, malformed row, invalid vertex, or out-of-range value is outside the contract and has no promised exception or response.

### Compatibility baseline

Version 1 freezes package `bmv.edge_limited_paths`, public class `EdgeLimitedPaths`, public no-argument construction, method name `distanceLimitedPathsExist`, parameter order/types `(int, int[][], int[][])`, and return type `boolean[]`. There is no CLI or HTTP compatibility surface.
