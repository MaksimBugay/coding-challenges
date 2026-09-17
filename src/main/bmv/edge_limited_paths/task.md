# Edge-Limited Path Queries

Adapted from LeetCode 1697, **Checking Existence of Edge Length Limited Paths**
(Hard):
https://leetcode.com/problems/checking-existence-of-edge-length-limited-paths/

This file paraphrases the original challenge for local interview practice.

## Problem

An undirected weighted graph contains `n` vertices numbered from `0` through
`n - 1`. Each entry in `edgeList` has the form `[from, to, weight]`. Parallel
edges between the same pair of vertices are allowed.

Each entry in `queries` has the form `[start, end, limit]`. For every query,
determine whether `start` and `end` are connected by some path for which every
edge has a weight strictly smaller than `limit`.

Return one boolean per query in the original query order.

## Java operation

```java
boolean[] distanceLimitedPathsExist(
    int n,
    int[][] edgeList,
    int[][] queries)
```

## Example 1

```text
n = 3
edgeList = [[0, 1, 2], [1, 2, 4], [2, 0, 8], [1, 0, 16]]
queries = [[0, 1, 2], [0, 2, 5]]

result = [false, true]
```

For the first query, an edge of weight `2` does not qualify because the limit is
strict. For the second query, `0 -> 1 -> 2` uses weights `2` and `4`, both below
`5`.

## Example 2

```text
n = 5
edgeList = [[0, 1, 10], [1, 2, 5], [2, 3, 9], [3, 4, 13]]
queries = [[0, 4, 14], [1, 4, 13]]

result = [true, false]
```

## Constraints

- `2 <= n <= 100_000`
- `1 <= edgeList.length <= 100_000`
- `1 <= queries.length <= 100_000`
- Every edge and query contains exactly three integers.
- All vertex identifiers are between `0` and `n - 1`.
- An edge never connects a vertex to itself.
- A query always names two different vertices.
- Edge weights and query limits are between `1` and `1_000_000_000`.
- Parallel edges may occur.

Inputs satisfying these constraints are the required domain. Invalid-input
behavior is not specified by this challenge.
