# Log Analysis architecture

## Status

- Phase: development complete
- Contract version: 1
- Design blockers: none
- Production declaration: `src/main/bmv/log_analysis/Result.java`
- Java compatibility target: 17
- Dependency or build changes: none
- Production acceptance: focused and complete Maven suites pass; Java 17 compatibility passes

Behavior, domain, and acceptance criteria are owned by [requirements.md](requirements.md). Public call semantics and examples are owned by [api-contract.md](api-contract.md).

## Reuse findings

Repository searches for the required method, stale-server terminology, request-window logic, and matching log/query operations found no implementation, caller, or test with this contract. `bmv.worcato.SlowProcessCollector` handles an unrelated bounded priority queue and cannot be reused. JDK lists and a primitive marker array are sufficient; no dependency or shared helper is justified.

## IF-001 — Stale-server count operation

Traces to REQ-001 through REQ-005.

```java
package bmv.log_analysis;

public class Result {
  public Result();

  public static List<Integer> getStaleServerCount(
      int n,
      List<List<Integer>> logData,
      List<Integer> query,
      int x);
}
```

The public no-argument constructor is Java's implicit default for the required public owner; callers need only the static operation. No production interface, service object, factory, or collaborator is warranted.

## IF-001 contract version 1

For each query value, inspect every log row. A server is active when at least one row for its ID has a timestamp `>= queryValue - x` and `<= queryValue`. Count each active server once, subtract the distinct count from `n`, and append the answer in query order.

Behavior outside the valid input domain is unspecified. The operation must not rely on or alter shared state. Input mutation and returned-list mutability are not contractual. There is no I/O, external side effect, collaborator, or resource lifecycle. Contract version 1 provides no explicit concurrent-use guarantee.

## Production implementation

1. Allocate the result list and one integer marker array indexed by valid server ID.
2. For each query position, use a distinct positive generation marker and set the active count to zero.
3. Scan all log rows. When a timestamp is within the inclusive window and the server is unmarked for this generation, mark it and increment the active count.
4. Append `n - activeCount`.

The generation marker avoids clearing an `n + 1` boolean array for every query while retaining a direct, readable scan. Since `q <= 1,000`, an `int` generation cannot overflow. Long arithmetic may be used for `queryValue - x`, although the valid numeric limits cannot overflow `int`.

For `m` logs and `q` queries, time is `O(mq)`, working space is `O(n)`, and total additional space including the returned list is `O(n + q)`. At the maxima this performs 1,000,000 log checks and stores at most 2,001 integers, which meets the stated domain without sorting or mutating inputs.

## Independent contract-test seam

The testing phase owns one abstract shared suite at `src/test/bmv/log_analysis/LogAnalysisContract.java`:

```java
abstract class LogAnalysisContract {
  @FunctionalInterface
  protected interface Operation {
    List<Integer> invoke(
        int n, List<List<Integer>> logData, List<Integer> query, int x);
  }

  protected abstract Operation operationFor(String scenarioId);
}
```

The shared suite owns all inputs and literal requirement-derived expected lists. `MockLogAnalysisContract` returns a Mockito mock `Operation` selected by scenario ID, with responses declared separately from assertion fixtures. `LogAnalysisContractTest` ignores the scenario ID and returns `Result::getStaleServerCount`. This preserves the concrete static API while running identical inputs and assertions against both bindings. Neither binding may compute expected results by invoking or reproducing production logic.

Intended test paths:

- `src/test/bmv/log_analysis/LogAnalysisContract.java`
- `src/test/bmv/log_analysis/MockLogAnalysisContract.java`
- `src/test/bmv/log_analysis/LogAnalysisContractTest.java`

Applicable commands:

- Design declaration: `mvn -DskipTests compile`
- Java 17 compatibility: `javac --release 17 -d /tmp/log-analysis-java17 src/main/bmv/log_analysis/Result.java`
- Mock contract: `mvn -Dtest=MockLogAnalysisContract test`
- Production contract: `mvn -Dtest=LogAnalysisContractTest test`
- Final suite: `mvn test`, including the resource-heavy `SlowProcessCollectorTest`

## WP-001 — Declare IF-001

- Owner: solution-design phase.
- Contract: version 1.
- Permitted file: `src/main/bmv/log_analysis/Result.java`.
- Work: add the minimal class and static method with an explicit fail-fast placeholder; implement no business behavior.
- Completion: declaration matches API-001 and both Maven and Java 17 compilation checks succeed.

## WP-002 — Build independent black-box tests

- Owner: testing phase.
- Contract: frozen version 1.
- Permitted files: the three intended test paths and handoff status/evidence updates.
- Dependency: WP-001 complete.
- Work completed: implemented TEST-001 through TEST-009 from [traceability.md](traceability.md) once in the shared suite, with a scenario-keyed Mockito binding and a direct production binding.
- Completion evidence: the mock suite passed all 9 scenarios after fixture review and repair; the production binding compiled but was not executed, assertions remain independent of implementation, and final Java review reported no actionable findings.

## WP-003 — Implement IF-001 / API-001

- Owner: development phase.
- Contract: frozen version 1.
- Permitted production file: `src/main/bmv/log_analysis/Result.java`.
- Dependencies: WP-001 and WP-002 complete.
- Reuse: existing declaration and JDK collections/arrays.
- Work completed: replaced the placeholder with the planned per-query scan and generation-based distinct-server marking.
- Expected complexity: `O(mq)` time, `O(n)` working space, and `O(n + q)` additional space including output.
- Completion evidence: focused production tests passed 9/9, `javac --release 17` succeeded, and the complete Maven suite passed 138/138 including `SlowProcessCollectorTest`; duplicate, complexity, threading, scope, and contract reviews passed.

Phases must not concurrently edit a shared file. Testing owns test files; development owns the production file during WP-003.

## Design decisions

- **ARCH-DEC-001:** Preserve the required concrete `Result` owner and static method.
- **ARCH-DEC-002:** Add no production abstraction, dependency, or build change.
- **ARCH-DEC-003:** Use the direct `O(mq)` scan because the explicit limits cap it at one million row checks and it naturally supports unsorted input.
- **ARCH-DEC-004:** Count server IDs distinctly per query with a reusable generation marker array.
- **ARCH-DEC-005:** Keep invalid input, returned-list mutability, input mutation, and concurrency behavior unspecified.
- **ARCH-DEC-006:** Keep source compatible with Java 17 while allowing the shared Maven project to remain on Java 25.
