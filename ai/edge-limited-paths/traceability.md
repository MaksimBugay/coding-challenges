# Edge-Limited Path Queries traceability

## Contract baseline

- Contract version: 1
- Public boundary: IF-001
- Java operation: API-001
- Work packages: WP-001 through WP-003

## Design and planned verification

| Requirement / criterion | Interface / API | Independent verification | Implementation / review |
| --- | --- | --- | --- |
| REQ-001 / AC-001-01 | IF-001 / API-001 | TEST-001 required example 1 | WP-003 |
| REQ-001 / AC-001-02 | IF-001 / API-001 | TEST-002 equality is excluded | WP-003 |
| REQ-001 / AC-001-03 | IF-001 / API-001 | TEST-003 qualifying indirect path | WP-003 |
| REQ-001 / AC-001-04 | IF-001 / API-001 | TEST-004 disconnected components | WP-003 |
| REQ-001 / AC-001-05 | IF-001 / API-001 | TEST-005 reverse traversal | WP-003 |
| REQ-002 / AC-002-01 | IF-001 / API-001 | TEST-006 result length and non-null result | WP-003 |
| REQ-002 / AC-002-02 | IF-001 / API-001 | TEST-007 non-sorted limits preserve order | WP-003 |
| REQ-002 / AC-002-03 | IF-001 / API-001 | TEST-008 duplicate queries | WP-003 |
| REQ-003 / AC-003-01 | IF-001 / API-001 | TEST-005 undirected traversal | WP-003 |
| REQ-003 / AC-003-02 | IF-001 / API-001 | TEST-009 parallel edges around strict limit | WP-003 |
| REQ-003 / AC-003-03 | IF-001 / API-001 | TEST-010 cycle with irrelevant heavy edge | WP-003 |
| REQ-004 / AC-004-01 | IF-001 / API-001 | TEST-011 minimum valid dimensions | WP-003 |
| REQ-004 / AC-004-02 | IF-001 / API-001 | TEST-012 weight/limit extrema | WP-003 |
| REQ-004 / AC-004-03 | IF-001 / API-001 | REVIEW-001 complexity and strict-sweep invariant | WP-003 |
| REQ-005 / AC-005-01 | IF-001 / API-001 | BUILD-001 declaration compile; production binding compile | WP-001, WP-002 |
| REQ-005 / AC-005-02 | IF-001 / API-001 | REVIEW-002 unchanged `pom.xml` and dependency tree | WP-001–WP-003 |
| REQ-005 / AC-005-03 | IF-001 / API-001 | REVIEW-003 scoped diff and duplicate search | WP-001–WP-003 |

## Contract test vectors

| Test ID | Input summary | Literal expected result |
| --- | --- | --- |
| TEST-001 | Required example 1 | `[false, true]` |
| TEST-002 | `n=2`, edges `[[0,1,2]]`, query `[[0,1,2]]` | `[false]` |
| TEST-003 | `n=3`, edges `[[0,1,2],[1,2,4]]`, query `[[0,2,5]]` | `[true]` |
| TEST-004 | `n=4`, edges `[[0,1,1],[2,3,1]]`, query `[[0,3,2]]` | `[false]` |
| TEST-005 | `n=3`, edges `[[0,1,3],[1,2,4]]`, query `[[2,0,5]]` | `[true]` |
| TEST-006 | Required example 2; assert non-null and length 2 as well as values | `[true, false]` |
| TEST-007 | API contract original-order example | `[true, false, true]` |
| TEST-008 | Duplicate `[[0,2,5],[0,2,5]]` over TEST-003 graph | `[true, true]` |
| TEST-009 | `n=2`, edges `[[0,1,7],[0,1,3]]`, queries `[[0,1,4],[0,1,3]]` | `[true, false]` |
| TEST-010 | `n=4`, edges `[[0,1,1],[1,2,2],[2,0,100],[2,3,3]]`, queries `[[0,3,4],[0,3,3]]` | `[true, false]` |
| TEST-011 | `n=2`, one edge and one query at minimum positive values: `[[0,1,1]]`, `[[0,1,2]]` | `[true]` |
| TEST-012 | `n=2`, edge weight `1_000_000_000`, queries with limits `1` and `1_000_000_000` | `[false, false]` |

Inputs outside the valid domain, mutation, concurrency, private helpers, and exact implementation structure have no test IDs because contract version 1 does not specify them. Maximum-scale suitability is established by algorithmic review rather than a brittle wall-clock assertion.

## Verification definitions

- **BUILD-001:** Run `mvn -DskipTests compile`, then compile and execute the focused bindings at their phases.
- **REVIEW-001:** Confirm sorted offline queries only see edges with `weight < limit`; confirm stated time/space bounds and overflow-safe comparisons.
- **REVIEW-002:** Confirm no dependency or build change.
- **REVIEW-003:** Search before and after implementation for equivalent graph/DSU behavior and inspect the final scoped diff.

## Implemented contract suite

All scenarios and assertions are implemented once in `src/test/bmv/edge_limited_paths/EdgeLimitedPathsContract.java`.

| Test ID | Shared test method |
| --- | --- |
| TEST-001 | `test001AnswersTheFirstRequirementExample` |
| TEST-002 | `test002ExcludesAnEdgeWhoseWeightEqualsTheLimit` |
| TEST-003 | `test003FindsAQualifyingIndirectPath` |
| TEST-004 | `test004ReturnsFalseForDisconnectedComponents` |
| TEST-005 | `test005TraversesUndirectedEdgesInReverse` |
| TEST-006 | `test006ReturnsOneNonNullResultPerQueryForTheSecondRequirementExample` |
| TEST-007 | `test007PreservesTheOriginalOrderOfQueriesWithUnsortedLimits` |
| TEST-008 | `test008AnswersDuplicateQueriesAtTheirOriginalIndexes` |
| TEST-009 | `test009UsesTheQualifyingParallelEdgeAndKeepsTheLimitStrict` |
| TEST-010 | `test010IgnoresAHeavyCycleEdge` |
| TEST-011 | `test011SupportsMinimumValidDimensionsAndWeights` |
| TEST-012 | `test012SupportsTheMaximumWeightAndLimitValues` |

`src/test/bmv/edge_limited_paths/MockEdgeLimitedPathsContract.java` binds the suite to a Mockito `Operation`. Its response map is keyed only by scenario ID and is declared separately from the assertion fixtures. Its name intentionally does not match ordinary Surefire `*Test` discovery.

`src/test/bmv/edge_limited_paths/EdgeLimitedPathsContractTest.java` binds the unchanged suite directly to `new EdgeLimitedPaths()::distanceLimitedPathsExist`. Production acceptance uses this binding with no external double.

## Testing-phase evidence

- Command: `mvn -Dtest=MockEdgeLimitedPathsContract test`
- Result: exit 0; 12 tests run, 0 failures, 0 errors, 0 skipped.
- Report: `target/surefire-reports/bmv.edge_limited_paths.MockEdgeLimitedPathsContract.txt`.
- Inspection: TEST-001 through TEST-012 each occur once in the shared suite and once in the independently declared response map.
- Meaning: this historical testing-phase run validated contract compilation, discovery, fixtures, assertions, and mock mechanics. It was not counted as production evidence.

## Production implementation and acceptance

| Requirement | Production implementation | Passing verification |
| --- | --- | --- |
| REQ-001 | `EdgeLimitedPaths.distanceLimitedPathsExist`: strict sorted-edge sweep and disjoint-set connectivity | TEST-001 through TEST-005 |
| REQ-002 | Indexed query records and writes to `answers[originalIndex]` | TEST-006 through TEST-008 |
| REQ-003 | Undirected `union(from, to)` with all parallel edge rows retained | TEST-005, TEST-009, TEST-010 |
| REQ-004 | Edge/query sorting plus path compression and union by size; integer comparisons use `Comparator.comparingInt` and `<` | TEST-011, TEST-012, REVIEW-001 |
| REQ-005 | `bmv.edge_limited_paths.EdgeLimitedPaths` public API; JDK-only implementation | focused and complete Maven runs, REVIEW-002, REVIEW-003 |

- Baseline command: `mvn -Dtest=EdgeLimitedPathsContractTest test` — exit 1; 12 tests run, 0 failures, 12 errors, 0 skipped. Every error was the design placeholder's `UnsupportedOperationException`, confirming the binding reached production.
- Focused command after implementation: `mvn -Dtest=EdgeLimitedPathsContractTest test` — exit 0; 12 tests run, 0 failures, 0 errors, 0 skipped. Report: `target/surefire-reports/bmv.edge_limited_paths.EdgeLimitedPathsContractTest.txt`.
- Complete command: `mvn test` — exit 0; 67 tests run, 0 failures, 0 errors, 0 skipped. Reports: `target/surefire-reports/`.
- `SlowProcessCollectorTest` completed in the full run: 1 test, 0 failures, 0 errors, 0 skipped.
- **REVIEW-001 complete:** Immediately before each query is answered, the disjoint set contains exactly edges with `weight < limit`. Sorting is `O(E log E + Q log Q)`; disjoint-set work is `O((E + Q) alpha(n))`; auxiliary storage is `O(n + E + Q)`.
- **REVIEW-002 complete:** `pom.xml` and dependency configuration were unchanged.
- **REVIEW-003 complete:** scoped search found no second graph/DSU implementation or remaining production placeholder; final changes stay within the exercise and its handoff.
