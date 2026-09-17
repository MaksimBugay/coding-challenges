# Recent Error Tracker architecture

## Status

- Phase: development complete
- Contract version: 1
- Design blockers: none
- Production implementation: `src/main/bmv/error_tracker/Result.java`
- Java compatibility target: 17
- Dependency or build changes: none
- Production acceptance: focused and complete Maven suites pass; Java 17 compatibility passes

Behavior, domain, and acceptance criteria are owned by [requirements.md](requirements.md). Public call semantics and examples are owned by [api-contract.md](api-contract.md).

## Reuse findings

Repository searches for error-tracker terminology, the required method, frequency aggregation, and matching `List<String>` operations found no implementation, caller, or test with this contract. Existing collection utilities solve unrelated exercises and have incompatible inputs and outputs. The required `Result` owner and static method are retained. JDK collections and `String` natural ordering are sufficient; no dependency or shared helper is justified.

## IF-001 — Recent error-code operation

Traces to REQ-001 through REQ-006.

```java
package bmv.error_tracker;

public class Result {
  public Result();

  public static List<String> getErrorCodes(
      int k,
      int t,
      List<Integer> timestamps,
      List<String> errorCodes);
}
```

The public no-argument constructor is Java's implicit default for the required public owner; callers need only the static operation. No production interface, service, factory, or collaborator is warranted.

## IF-001 contract version 1

For valid inputs, define `lowerBound = timestamps.get(n - 1) - t + 1`. Count each `errorCodes.get(i)` exactly when `timestamps.get(i) >= lowerBound`; sorted timestamps ensure no timestamp exceeds the final timestamp. The non-null result contains each code whose count is at least `k` exactly once, in ascending Java `String` natural order. If none qualifies, the result is empty.

Behavior outside the valid input domain is unspecified. The operation must not rely on or alter shared state. Input mutation and returned-list mutability are not contractual. There is no I/O, external side effect, collaborator, or resource lifecycle. Contract version 1 provides no explicit concurrent-use guarantee.

## Planned implementation

1. Calculate the inclusive lower timestamp from the last element and `t` using `long` arithmetic, even though valid constraints keep the result within `int` range.
2. Walk backward from the final entry until reaching a timestamp below the lower bound, incrementing a `HashMap<String, Integer>` for each in-window code.
3. Collect map keys whose counts are at least `k`, sort them with `List.sort(null)`, and return the list.

Let `w` be the number of entries in the window, `u` the number of distinct in-window codes, and `q` the number of qualifying codes. Expected time is `O(w + q log q)` with ordinary hash-map assumptions; worst-case library hash behavior is governed by the JDK. Space is `O(u + q)`. These bounds are suitable for `n <= 100,000`. Walking from the end exploits the stated timestamp ordering and avoids inspecting entries before the window.

## Independent contract-test seam

The testing phase owns one abstract shared suite at `src/test/bmv/error_tracker/RecentErrorTrackerContract.java`:

```java
abstract class RecentErrorTrackerContract {
  @FunctionalInterface
  protected interface Operation {
    List<String> invoke(
        int k, int t, List<Integer> timestamps, List<String> errorCodes);
  }

  protected abstract Operation operationFor(String scenarioId);
}
```

The shared suite owns all inputs and literal requirement-derived expected lists. `MockRecentErrorTrackerContract` returns a Mockito mock `Operation` selected by scenario ID, with responses declared separately from assertion fixtures. `RecentErrorTrackerContractTest` ignores the scenario ID and returns `Result::getErrorCodes`. This preserves the concrete static API while running identical inputs and assertions against both bindings. Neither binding may compute expected results by invoking or reproducing production logic.

Intended test paths:

- `src/test/bmv/error_tracker/RecentErrorTrackerContract.java`
- `src/test/bmv/error_tracker/MockRecentErrorTrackerContract.java`
- `src/test/bmv/error_tracker/RecentErrorTrackerContractTest.java`

Applicable commands:

- Design declaration: `mvn -DskipTests compile`
- Java 17 compatibility: compile the production declaration with `javac --release 17` into a temporary directory
- Mock contract: `mvn -Dtest=MockRecentErrorTrackerContract test`
- Production contract: `mvn -Dtest=RecentErrorTrackerContractTest test`
- Final suite: `mvn test`, including the resource-heavy `SlowProcessCollectorTest`

## WP-001 — Declare IF-001

- Owner: solution-design phase.
- Contract: version 1.
- Permitted file: `src/main/bmv/error_tracker/Result.java`.
- Work: add the minimal class and static method with an explicit fail-fast placeholder; implement no business behavior.
- Completion: declaration matches API-001 and both Maven and Java 17 compilation checks succeed.

## WP-002 — Build independent black-box tests

- Owner: testing phase.
- Contract: frozen version 1.
- Permitted files: the three intended test paths and handoff status/evidence updates.
- Dependency: WP-001 complete.
- Work completed: implemented TEST-001 through TEST-010 from [traceability.md](traceability.md) once in the shared suite, with a scenario-ID-keyed Mockito binding and a direct production binding.
- Completion evidence: the mock suite passed all 10 scenarios after review repair; the production binding compiled but was not executed, and assertions remain independent of implementation.

## WP-003 — Implement IF-001 / API-001

- Owner: development phase.
- Contract: frozen version 1.
- Permitted production file: `src/main/bmv/error_tracker/Result.java`.
- Dependencies: WP-001 and WP-002 complete.
- Reuse: existing declaration and JDK collections.
- Work completed: replaced the placeholder with the planned backward scan, hash-map count filtering, and lexicographic sort without changing shared test assertions.
- Expected complexity: `O(w + q log q)` expected time and `O(u + q)` space.
- Completion evidence: focused production tests passed 10/10, `javac --release 17` succeeded, and the complete Maven suite passed 129/129 including `SlowProcessCollectorTest`; complexity, duplicate, scope, and final implementation reviews passed.

Phases must not concurrently edit a shared file. Testing owns test files; development owns the production file during WP-003.

## Design decisions

- **ARCH-DEC-001:** Preserve the required concrete `Result` owner and static method.
- **ARCH-DEC-002:** Add no production abstraction, dependency, or build change.
- **ARCH-DEC-003:** Exploit sorted timestamps with a backward window scan, then sort only qualifying distinct codes.
- **ARCH-DEC-004:** Use Java natural string order and exact code equality.
- **ARCH-DEC-005:** Keep invalid input, returned-list mutability, input mutation, and concurrency behavior unspecified.
- **ARCH-DEC-006:** Keep source compatible with Java 17 while allowing the shared Maven project to remain on Java 25.
