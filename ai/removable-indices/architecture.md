# Removable Indices architecture

## Status

- Phase: development complete
- Contract version: 1
- Design blockers: none
- Production declaration: `src/main/bmv/removable_indices/Result.java`
- Dependency or build changes: none
- Testing phase: shared contract, mock binding, and production binding compile; mock suite passes
- Production implementation: accepted against the focused and complete suites

Behavior, domain, and acceptance criteria are owned by [requirements.md](requirements.md). Public call semantics and examples are owned by [api-contract.md](api-contract.md).

## Reuse findings

Repository searches for the method name, task terminology, list-returning static methods, and character-removal logic found no matching implementation, caller, or test. Unrelated substring and storage-removal code has a different contract and is unsuitable. The screenshot's `Result` owner and static method are retained. JDK `String` and `List` facilities are sufficient; no dependency is needed.

## IF-001 — Removable-index operation

Traces to REQ-001 through REQ-005.

```java
package bmv.removable_indices;

public class Result {
  public Result();

  public static List<Integer> getRemovableIndices(String str1, String str2);
}
```

The public no-argument constructor is the Java default on the required public owner; callers need only the static operation. No production interface, service, factory, or collaborator is justified.

## IF-001 contract version 1

Valid inputs satisfy REQ-004. For each index `i` from `0` through `str1.length() - 1`, the result contains `i` exactly when:

```text
str1.substring(0, i) + str1.substring(i + 1) == str2
```

String equality above means exact value equality. Valid indexes occur once each in increasing order. If that set is empty, the result is exactly `List.of(-1)`. The result is non-null.

Behavior outside the valid input domain is unspecified. Strings are immutable, and the operation has no external side effect, collaborator, mutable cross-call state, I/O, or resource lifecycle. Contract version 1 makes no concurrent-use promise beyond ordinary behavior of a stateless implementation.

## Planned implementation

Let `m = str2.length()`; valid removal indexes range from `0` through `m`.

1. Scan left while `str1[i] == str2[i]`. The first mismatch position, or `m` if none exists, is the greatest possible removal index because every character before a removed index must already align.
2. Scan right while `str1[i + 1] == str2[i]`. One position after the last mismatch, or `0` if none exists, is the least possible removal index because every character after a removed index must align with a one-position shift.
3. Every index in the inclusive overlap `[least, greatest]` satisfies both independent conditions. Return that interval in increasing order, or `[-1]` if the interval is empty.

Each scan visits at most `m` positions, and every returned index must be materialized. Time is `O(n + k)`, which is `O(n)` because `k <= n`; auxiliary working space is `O(1)`, with `O(k)` required result space. Here `n = str1.length()` and `k` is the number of returned valid indexes. The implementation must compare characters directly and must not construct a candidate string for every index, which would be quadratic at the stated maximum.

## Independent contract-test seam

The testing phase owns one abstract shared suite at `src/test/bmv/removable_indices/RemovableIndicesContract.java`:

```java
abstract class RemovableIndicesContract {
  @FunctionalInterface
  protected interface Operation {
    List<Integer> invoke(String str1, String str2);
  }

  protected abstract Operation operationFor(String scenarioId);
}
```

The shared suite owns every scenario input and literal requirement-derived expected list. `MockRemovableIndicesContract` returns a Mockito mock `Operation` selected by scenario ID; its response declarations remain separate from assertion fixtures. `RemovableIndicesContractTest` ignores the scenario ID and returns `Result::getRemovableIndices`. This keeps the exact static production API while running unchanged inputs and assertions against both bindings. Neither binding may calculate expected results by calling or reproducing production logic.

Intended test paths:

- `src/test/bmv/removable_indices/RemovableIndicesContract.java`
- `src/test/bmv/removable_indices/MockRemovableIndicesContract.java`
- `src/test/bmv/removable_indices/RemovableIndicesContractTest.java`

Applicable commands:

- Design declaration: `mvn -DskipTests compile`
- Mock contract: `mvn -Dtest=MockRemovableIndicesContract test`
- Production contract: `mvn -Dtest=RemovableIndicesContractTest test`
- Final suite: `mvn test`, including the resource-heavy `SlowProcessCollectorTest`

## WP-001 — Declare IF-001

- Owner: solution-design phase.
- Contract: version 1.
- Permitted file: `src/main/bmv/removable_indices/Result.java`.
- Work: add the minimal class and static method with an explicit fail-fast placeholder; implement no business behavior.
- Completion: declaration matches API-001 and `mvn -DskipTests compile` succeeds.

## WP-002 — Build independent black-box tests

- Owner: testing phase; complete.
- Contract: frozen version 1.
- Permitted files: the three intended test paths and handoff status/evidence updates.
- Dependency: WP-001 complete.
- Work completed: implemented TEST-001 through TEST-010 from [traceability.md](traceability.md) once in the shared suite, with a scenario-ID-keyed Mockito binding and a direct production binding.
- Completion evidence: the explicitly selected mock suite passed all 10 shared scenarios; its response map is separate from assertion fixtures. The production binding compiled but was not executed, so production correctness remains unverified.

## WP-003 — Implement IF-001 / API-001

- Owner: development phase; complete.
- Contract: frozen version 1.
- Permitted production file: `src/main/bmv/removable_indices/Result.java`.
- Dependencies: WP-001 and WP-002 complete.
- Reuse: existing declaration and JDK strings/lists.
- Work completed: replaced the fail-fast placeholder with the frozen two-scan overlap algorithm. Shared assertions and bindings were unchanged.
- Completion evidence: the focused production suite passed 10/10 and the complete Maven suite passed 77/77, including the 20-million-record `SlowProcessCollectorTest`; complexity and final scope reviews passed; no placeholder, duplicate behavior, unrelated edit, or build/dependency change remains.

Phases must not concurrently edit a shared file. Testing owns test files; development owns the production file during WP-003.

## Design decisions

- **ARCH-DEC-001:** Preserve the required concrete `Result` owner and static method.
- **ARCH-DEC-002:** Add no production abstraction or dependency.
- **ARCH-DEC-003:** Use prefix/shifted-suffix overlap to enumerate all answers in linear time.
- **ARCH-DEC-004:** Keep invalid input and concurrency behavior unspecified.
- **ARCH-DEC-005:** Keep mock responses separate from shared assertion fixtures and pass only a scenario ID to the binding seam.
