# Architecture — String Similarity

Contract version: **v1**

## Reuse findings

Searched `src/` for `similarity`, `z-array`, `zFunction`, `z function`,
`zArray` (case-insensitive). No existing implementation found. No JDK
built-in computes a Z-array or prefix-similarity sum, so a small
hand-written algorithm is required, matching the exercise's own intent
(this is a classic string-algorithms exercise expected to be implemented
directly, not delegated to a library).

## Boundary

- **IF-001**: `bmv.string_similarity.Result.stringSimilarity(String s)`
  - Signature: `public static int stringSimilarity(String s)` — fixed by
    the HackerRank starter contract; must not be renamed or have its
    signature altered.
  - Preconditions: `s != null`, `1 ≤ s.length() ≤ 100000`,
    `s` matches `[a-z]+`. Values outside these bounds are outside the
    stated contract; behavior is unspecified (not validated defensively,
    consistent with "smallest complete solution" — HackerRank guarantees
    input meets constraints).
  - Postcondition: returns the sum, over `i` in `0..s.length()-1`, of the
    length of the longest common prefix between `s` and `s.substring(i)`.
  - No side effects, no mutation of `s`, pure function, no I/O.
  - Threading: stateless static method; trivially thread-safe (no shared
    mutable state).
  - Internal accumulation uses `long` (see requirements.md Q-001 decision);
    the return statement narrows to `int` exactly once, at the boundary.

- **IF-002 (out of scope for unit testing)**: the HackerRank-provided
  stdin/stdout adapter (reads `t`, then `t` lines, prints `t` answers via
  `Result.stringSimilarity`). This is unmodified HackerRank boilerplate,
  only exercised via the browser Run Code step (see task.md), not via
  local JUnit tests. No local class is created for it; local tests call
  `Result.stringSimilarity` directly.

## Declarations (`src/main`)

- `src/main/bmv/string_similarity/Result.java`
  - `public final class Result` (package `bmv.string_similarity`)
  - `public static int stringSimilarity(String s)` — placeholder body in
    this phase: `throw new UnsupportedOperationException("not implemented — design phase placeholder, IF-001, contract v1")`.
    Development phase must replace this with the real algorithm; no other
    behavior is implemented in this phase.

No interface is introduced: `stringSimilarity` is a single required static
method with no variation point, so an interface would be a speculative,
unrequested abstraction (AGENTS.md SOLID guidance).

## Test construction seam

Tests call `bmv.string_similarity.Result.stringSimilarity(String)` directly
— it is already a pure static function with no collaborators to mock, so no
additional seam (interface, factory, DI) is needed. The same test method
bodies run unchanged against:

- **Mock/skeleton binding** (testing phase): the placeholder above, which
  is expected to fail every behavioral assertion (it throws), proving the
  tests actually exercise the method and are not vacuously true.
- **Production binding** (development phase): the real implementation,
  same method signature, same package/class.

No Mockito mocking is required for this exercise (no collaborators to
isolate); the project's existing Mockito agent configuration
(`argLine` in `pom.xml`) is unaffected and requires no changes.

## Work packages

| ID | Description | Requirement(s) | Files | Depends on | Complexity target | Completion criteria |
| --- | --- | --- | --- | --- | --- | --- |
| WP-001 | Design: declare `Result` with placeholder body | REQ-001, REQ-002 | `src/main/bmv/string_similarity/Result.java` | — | O(1) (no logic yet) | Compiles with `mvn -DskipTests compile` |
| WP-002 | Testing: black-box tests for AC-001-01..05, AC-002-01 | REQ-001, REQ-002 | `src/test/bmv/string_similarity/ResultTest.java` | WP-001 | n/a (test code) | Tests compile and run; all fail against the placeholder (expected), reviewed by `java-code-reviewer` |
| WP-003 | Development: implement Z-array algorithm, remove placeholder | REQ-001, REQ-002 | `src/main/bmv/string_similarity/Result.java` | WP-001, WP-002 | O(n) time, O(n) auxiliary space per call | All WP-002 tests pass; `mvn -Dtest=ResultTest test` green |

Ownership: WP-001 and WP-003 both touch `Result.java` but in disjoint
phases (design placeholder, then development replacement) — no concurrent
edits. WP-002 owns `ResultTest.java` exclusively.

## Build validation

Command: `mvn -DskipTests compile`
Result: to be recorded in `execution-log.md` after running.
