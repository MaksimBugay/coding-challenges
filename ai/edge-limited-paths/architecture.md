# Edge-Limited Path Queries architecture

## Status

- Phase: development complete
- Contract version: 1
- Design blockers: none
- Testing phase: complete; shared contract and both bindings compile
- Production implementation: `src/main/bmv/edge_limited_paths/EdgeLimitedPaths.java`
- Dependency or build changes planned: none

## Reuse findings

Repository searches found no implementation, caller, test, graph abstraction, or disjoint-set implementation matching this exercise. The existing `bmv.Solution` and package-local classes named `Solution` solve unrelated challenges and must not be coupled to this one. JDK arrays and sorting are sufficient; no project dependency is needed.

## IF-001 — Edge-limited path query operation

Traces to REQ-001 through REQ-005:

```java
package bmv.edge_limited_paths;

public class EdgeLimitedPaths {
  public EdgeLimitedPaths();

  public boolean[] distanceLimitedPathsExist(
      int n,
      int[][] edgeList,
      int[][] queries);
}
```

No production interface, service layer, or dependency-injection mechanism is justified. The concrete class and its method are the complete production boundary.

## IF-001 contract version 1

Valid inputs are exactly those described by REQ-004. For each query index `i`, the returned array has `result[i] == true` if and only if a path connects `queries[i][0]` and `queries[i][1]` using only edges with weights strictly less than `queries[i][2]`. The result is non-null and has `queries.length` elements.

Invalid-input behavior, input mutation, and concurrent invocation behavior are unspecified. The class requires no collaborator, mutable cross-call state, or resource lifecycle. Each valid call's required result depends only on its supplied values.

## Planned implementation

Use an offline disjoint-set union sweep:

1. Order edges by ascending weight.
2. Pair every query with its original index and order those records by ascending limit.
3. Before answering a query, union every edge whose weight is strictly less than that query's limit.
4. Answer by comparing the roots of the query endpoints and store the value at the original index.

The strict comparison in step 3 is the key invariant. After it, the disjoint-set components are exactly the graph components induced by edges eligible for the current query. Sorting costs `O(E log E + Q log Q)`; union/find costs `O((E + Q) alpha(n))`; storage is `O(n + E + Q)`. Integer comparison must use `Integer.compare`, avoiding subtraction overflow.

Whether production sorts caller-owned arrays or copies their rows is an implementation choice because mutation is unspecified. The recommended implementation avoids mutating `queries` because it needs original indexes, and may sort a shallow copy of `edgeList` references.

## Independent contract-test seam

Stage 3 should own one abstract contract suite at `src/test/bmv/edge_limited_paths/EdgeLimitedPathsContract.java`:

```java
abstract class EdgeLimitedPathsContract {
  @FunctionalInterface
  protected interface Operation {
    boolean[] invoke(int n, int[][] edgeList, int[][] queries);
  }

  protected abstract Operation operationFor(String scenarioId);
}
```

The abstract suite owns immutable test-vector construction and literal expected arrays. `MockEdgeLimitedPathsContract` supplies a scenario-ID keyed mock operation whose response declarations remain separate from assertion fixtures. The production-backed `EdgeLimitedPathsContractTest` ignores the scenario ID and returns `new EdgeLimitedPaths()::distanceLimitedPathsExist`. This preserves the concrete public API while letting identical inputs and assertions run against both bindings. Neither binding may derive expected values from production logic.

Planned test paths:

- `src/test/bmv/edge_limited_paths/EdgeLimitedPathsContract.java`
- `src/test/bmv/edge_limited_paths/MockEdgeLimitedPathsContract.java`
- `src/test/bmv/edge_limited_paths/EdgeLimitedPathsContractTest.java`

Planned commands:

- Design declaration: `mvn -DskipTests compile`
- Mock contract: `mvn -Dtest=MockEdgeLimitedPathsContract test`
- Production contract: `mvn -Dtest=EdgeLimitedPathsContractTest test`
- Final complete suite: `mvn test` (including the resource-heavy `SlowProcessCollectorTest`)

## WP-001 — Declare IF-001

- Owner: solution-design phase.
- Contract: version 1.
- Permitted production file: `src/main/bmv/edge_limited_paths/EdgeLimitedPaths.java`.
- Work completed in solution design: added the minimal public class, implicit public no-argument construction, and public method with a temporary fail-fast placeholder.
- Completion evidence: declaration matched API-001 and `mvn -DskipTests compile` succeeded.

## WP-002 — Build independent black-box tests

- Owner: testing phase; complete.
- Contract: frozen version 1.
- Permitted files: the three planned test paths above and handoff status/evidence files.
- Dependency: WP-001 complete.
- Work: implement TEST-001 through TEST-012 from `traceability.md`, first with the independent mock binding.
- Completion evidence: the explicitly selected mock contract ran all 12 shared tests successfully without executing production behavior; assertions and responses have separate declarations. The production binding compiles and remains for development acceptance.

## WP-003 — Implement IF-001 / API-001

- Owner: development phase; complete.
- Contract: frozen version 1.
- Permitted production file: `src/main/bmv/edge_limited_paths/EdgeLimitedPaths.java`.
- Dependencies: WP-001 and WP-002 complete.
- Reuse: JDK sorting plus a narrow private disjoint-set implementation.
- Work completed: replaced the fail-fast placeholder with the offline edge/query sweep and a private disjoint set using path compression and union by size. Shared test assertions were unchanged.
- Completion evidence: valid-domain behavior passed the focused and complete suites; complexity matches REQ-004; no production placeholder or duplicate implementation remains; scope and dependency reviews are clean.

No concurrent phase may edit a shared file. The tester owns test files; the developer owns the production file during WP-003.

## Design decisions

- **ARCH-DEC-001:** Keep one concrete public production class and method.
- **ARCH-DEC-002:** Use offline query processing and disjoint-set union.
- **ARCH-DEC-003:** Add no production abstraction or dependency.
- **ARCH-DEC-004:** Leave invalid input, mutation, and concurrency unspecified.
- **ARCH-DEC-005:** Keep original query indexes in explicit query records.
- **ARCH-DEC-006:** Use a test-scoped functional seam, preserving the concrete public API.
- **ARCH-DEC-007:** Use developer-selected `bmv.edge_limited_paths.EdgeLimitedPaths` as the public owner type.
