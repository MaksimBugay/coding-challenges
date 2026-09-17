# Edge-Limited Path Queries requirements

## Status

- Phase: solution design
- Contract version: 1
- Blocking clarifications: none

## Source inventory

| ID | Source | Material used |
| --- | --- | --- |
| SRC-001 | `src/main/bmv/edge_limited_paths/task.md`, Problem and Java operation | Local authoritative behavior and method signature |
| SRC-002 | `src/main/bmv/edge_limited_paths/task.md`, Examples 1–2 | Local authoritative examples and strict-limit illustration |
| SRC-003 | `src/main/bmv/edge_limited_paths/task.md`, Constraints | Required valid input domain |
| SRC-004 | `src/main/bmv/edge_limited_paths/task-img1.png` | Original statement and example 1; readable and consistent with SRC-001–003 |
| SRC-005 | `src/main/bmv/edge_limited_paths/task-img2.png` | Original example 2 and constraints; readable and consistent with SRC-001–003 |
| SRC-006 | LeetCode 1697 URL referenced by SRC-001 | Read on 2026-09-14; confirms behavior, examples, and constraints |
| SRC-007 | `README.md` and `pom.xml` | JDK 25, Maven 3.9+, source roots, test/build configuration |
| SRC-008 | repository production/test search | No edge-limited-path declaration, caller, or dedicated test exists |

The raw requirements directory contains exactly the Markdown file and two PNG files listed above. No nested or unreadable material was found.

## Scope and requirements

### REQ-001 — Evaluate every query

For each valid query `[start, end, limit]`, return `true` exactly when the undirected graph contains a path from `start` to `end` on which every traversed edge has `weight < limit`. Sources: SRC-001, SRC-004, SRC-006.

- **AC-001-01:** Example 1 returns `[false, true]`.
- **AC-001-02:** An edge whose weight equals the limit does not qualify.
- **AC-001-03:** A qualifying multi-edge path returns `true` even when no qualifying direct edge exists.
- **AC-001-04:** A query between disconnected components returns `false`.
- **AC-001-05:** Either endpoint ordering has the same connectivity result because edges are undirected.

### REQ-002 — Preserve query correspondence

Return a non-null `boolean[]` with exactly one element per query, at the same index as that query in the supplied array. Sources: SRC-001, SRC-006.

- **AC-002-01:** For `q = queries.length`, the result length is `q`.
- **AC-002-02:** Results for queries with non-sorted limits remain in their original query order.
- **AC-002-03:** Duplicate queries produce equal results at their respective indexes.

### REQ-003 — Interpret the graph model

Treat every edge `[from, to, weight]` as undirected and allow parallel edges between the same vertices. Sources: SRC-001, SRC-003, SRC-004, SRC-006.

- **AC-003-01:** A path may traverse an edge from `to` to `from`.
- **AC-003-02:** A lighter qualifying parallel edge can make a query true despite another parallel edge being at or above the limit.
- **AC-003-03:** Cycles and irrelevant heavier edges do not alter the required answer.

### REQ-004 — Support the complete valid domain

Support `2 <= n <= 100_000`; 1–100,000 edges and queries; exactly three integers per row; vertex IDs in `[0, n - 1]`; distinct edge endpoints and distinct query endpoints; and weights/limits in `[1, 1_000_000_000]`. Sources: SRC-003, SRC-005, SRC-006.

- **AC-004-01:** Minimum valid dimensions complete normally.
- **AC-004-02:** Values at `1` and `1_000_000_000` are compared without overflow.
- **AC-004-03:** A design review establishes time `O(E log E + Q log Q + (E + Q) alpha(n))` and auxiliary space `O(n + E + Q)`, sufficient for the stated maxima.

### REQ-005 — Preserve the required Java operation and project constraints

Expose a public instance operation named `distanceLimitedPathsExist` with parameters `(int n, int[][] edgeList, int[][] queries)` and return type `boolean[]`, under the existing JDK 25/Maven source layout. Sources: SRC-001, SRC-007.

- **AC-005-01:** Production declaration and its direct production test binding compile with Maven.
- **AC-005-02:** No new dependency or build change is needed.
- **AC-005-03:** The implementation remains confined to this independent exercise.

## Exclusions and unspecified behavior

- Inputs outside REQ-004 are outside the challenge contract. No exception type, validation order, or fallback result is specified or planned for testing.
- The sources do not specify whether input arrays may be reordered or otherwise mutated. Contract version 1 will make no preservation assertion.
- The sources define no thread-safety guarantee, shared lifecycle, I/O, persistence, security boundary, or external resource.
- No CLI or HTTP boundary is requested.

## Decisions, assumptions, and clarification

- **DEC-001:** Treat local `task.md` as authoritative. The images and referenced LeetCode statement corroborate it and introduce no conflict.
- **DEC-002:** Use offline sorting plus disjoint-set union as the planned implementation because it directly satisfies the maximum sizes without adding dependencies.
- **DEC-003:** Expected results will be literal and requirement-derived; the production algorithm will not be used as a test oracle.
- **ASM-001 (non-blocking):** Maven and JDK versions remain as configured; no dependency work is expected.
- **Q-001 (resolved 2026-09-14):** The developer selected `bmv.edge_limited_paths.EdgeLimitedPaths` with public no-argument construction as the owner of the specified operation.
