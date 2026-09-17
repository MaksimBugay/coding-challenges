# Log Analysis traceability

## Contract baseline

- Contract version: 1
- Public boundary: IF-001
- Java operation: API-001
- Work packages: WP-001 through WP-003
- Java compatibility target: 17

## Requirement-to-verification map

| Requirement / criterion | Interface / API | Planned independent verification | Implementation / review |
| --- | --- | --- | --- |
| REQ-001 / AC-001-01 through AC-001-03 | IF-001 / API-001 | TEST-003 exact lower/upper boundaries and adjacent excluded timestamps | WP-003 |
| REQ-001 / AC-001-04 | IF-001 / API-001 | TEST-002 Sample Case 0 unsorted logs | WP-003 |
| REQ-002 / AC-002-01 | IF-001 / API-001 | TEST-004 repeated requests from one server | WP-003 |
| REQ-002 / AC-002-02, AC-002-03 | IF-001 / API-001 | TEST-005 all active and none active | WP-003 |
| REQ-002 / AC-002-04 | IF-001 / API-001 | TEST-001 statement example | WP-003 |
| REQ-002 / AC-002-05 | IF-001 / API-001 | TEST-002 Sample Case 0 | WP-003 |
| REQ-003 / AC-003-01 | IF-001 / API-001 | TEST-002 unsorted query order | WP-003 |
| REQ-003 / AC-003-02 | IF-001 / API-001 | TEST-006 duplicate queries and positional answers | WP-003 |
| REQ-004 / AC-004-01 | IF-001 / API-001 | TEST-007 single-server/log/query minimum | WP-003 |
| REQ-004 / AC-004-02 | IF-001 / API-001 | TEST-008 maximum `n`, `m`, and `q` | WP-003 |
| REQ-004 / AC-004-03 | IF-001 / API-001 | REVIEW-001 planned complexity and bounded storage | WP-003 |
| REQ-005 / AC-005-01 | IF-001 / API-001 | BUILD-001 Maven declaration and bindings compile | WP-001, WP-002 |
| REQ-005 / AC-005-02 | IF-001 / API-001 | BUILD-002 `javac --release 17` compatibility check | WP-001, WP-003 |
| REQ-005 / AC-005-03 | IF-001 / API-001 | TEST-009 direct static production binding | WP-001, WP-002, WP-003 |
| REQ-005 / AC-005-04 | IF-001 / API-001 | REVIEW-002 scoped diff and duplicate search | WP-001 through WP-003 |

## Black-box scenarios and methods

All methods are declared once in `src/test/bmv/log_analysis/LogAnalysisContract.java` and inherited unchanged by both bindings.

| Test ID and method | Scenario | Exact expected result |
| --- | --- | --- |
| TEST-001 `test001ReturnsTheStatementExampleCounts` | Statement example: `n=3`, logs `[[3,3],[2,6],[1,5]]`, queries `[10,11]`, `x=5` | `[1,2]` |
| TEST-002 `test002HandlesUnsortedLogsAndQueriesFromSampleCase0` | Sample Case 0: `n=6`, logs `[[3,2],[4,3],[2,6],[6,3]]`, queries `[3,2,6]`, `x=2` | `[3,5,5]` |
| TEST-003 `test003IncludesBothBoundariesAndExcludesAdjacentTimes` | `n=4`, logs at lower boundary 5, upper boundary 10, and adjacent times 4 and 11; query 10, `x=5` | `[2]` |
| TEST-004 `test004CountsARepeatedServerOnlyOnce` | `n=3`, three in-window rows for server 1; query 5, `x=1` | `[2]` |
| TEST-005 `test005ReturnsZeroForAllActiveAndNForNoneActive` | `n=3`, each server logged at time 5; queries `[5,10]`, `x=1` | `[0,3]` |
| TEST-006 `test006PreservesDuplicateUnsortedQueryPositions` | `n=3`, logs `[[1,3],[2,8],[3,8]]`, queries `[8,3,8]`, `x=1` | `[1,2,1]` |
| TEST-007 `test007SupportsTheSingleServerLogAndQueryMinimum` | `n=1`, logs `[[1,1]]`, queries `[1]`, `x=1` | `[0]` |
| TEST-008 `test008SupportsMaximumServerLogAndQueryCounts` | `n=m=q=1,000`; 1,000 servers each logged once at time 100,000; 1,000 queries at 100,000; `x=1` | list of 1,000 zeros |
| TEST-009 `test009InvokesTheExactPublicOperationWithALiteralCase` | Literal minimum-like case; production binding uses `Result::getStaleServerCount` | `[1]` |

Expected values must be literal or independently constructed. TEST-008 may use `Collections.nCopies(1_000, 0)` as its expected value; it must not derive the expected result from production output.

## Planned non-test verification

- **BUILD-001:** `mvn -DskipTests compile` during design, then Maven test compilation during testing.
- **BUILD-002:** Compile the declaration and final implementation with `javac --release 17` to a temporary output directory.
- **REVIEW-001:** Confirm the planned/final algorithm performs `O(mq)` work, uses `O(n)` working space and `O(n + q)` additional space including output, suitable for the stated maxima.
- **REVIEW-002:** Search for another implementation or copied request-window rule and inspect the scoped diff for unrelated changes.

Every REQ-001 through REQ-005 criterion maps to IF-001/API-001, an independent test or review, and its implementation work package.

## Bindings and testing-phase evidence

- `src/test/bmv/log_analysis/MockLogAnalysisContract.java` binds the shared suite to a Mockito `Operation`. Its response map is declared independently by stable scenario ID, and the binding receives no assertion fixture. The class name intentionally avoids ordinary Surefire `*Test` discovery.
- `src/test/bmv/log_analysis/LogAnalysisContractTest.java` binds the same suite directly to `Result::getStaleServerCount`. Development must run this binding without changing shared assertions.
- Mock command after fixture repair: `mvn -Dtest=MockLogAnalysisContract test`.
- Result: exit 0; 9 tests run, 0 failures, 0 errors, 0 skipped.
- Report: `target/surefire-reports/bmv.log_analysis.MockLogAnalysisContract.txt`.
- Production behavior was not executed. The successful mock stage establishes compilation, discovery, fixtures, assertions, maximum-size fixture construction, and binding mechanics only.
- Stable-ID inspection found every TEST-001 through TEST-009 once in the shared suite and once in the independently declared response map.
- Final Java review: no actionable findings within the three testing-phase Java files after TEST-006 was strengthened to distinguish query positions with expected results `[1,2,1]`.

## Production implementation and acceptance

| Requirement | Production implementation | Passing verification |
| --- | --- | --- |
| REQ-001 | Compares every request timestamp to both inclusive window boundaries for each query | TEST-001 through TEST-003, TEST-005, TEST-006 |
| REQ-002 | Uses a server-ID marker array and one generation per query so repeated requests count once | TEST-001 through TEST-005, TEST-007 through TEST-009 |
| REQ-003 | Appends one result while iterating queries in their supplied order | TEST-001, TEST-002, TEST-005, TEST-006 |
| REQ-004 | Uses a bounded `m × q` scan and an `n + 1` marker array | TEST-007, TEST-008, REVIEW-001 |
| REQ-005 | Preserves the required public static method and uses Java 17-compatible JDK APIs | focused and complete Maven runs, BUILD-002, REVIEW-002 |

- Baseline production command: `mvn -Dtest=LogAnalysisContractTest test` — exit 1; 9 tests run, 0 failures, 9 errors, 0 skipped. Every error was the intentional design placeholder's `UnsupportedOperationException`, confirming that the direct binding reached production.
- Focused production command after implementation: `mvn -Dtest=LogAnalysisContractTest test` — exit 0; 9 tests run, 0 failures, 0 errors, 0 skipped. Report: `target/surefire-reports/bmv.log_analysis.LogAnalysisContractTest.txt`.
- Java 17 command: `javac --release 17` compiled `src/main/bmv/log_analysis/Result.java` into a temporary output directory — exit 0.
- Complete command: `mvn test` — exit 0; 138 tests across 19 discovered suites, 0 failures, 0 errors, 0 skipped. Reports: `target/surefire-reports/`.
- `SlowProcessCollectorTest` completed its 20-million-record workload: 1 test, 0 failures, 0 errors, 0 skipped.
- **REVIEW-001 complete:** for `m` log rows, `q` queries, and `n` servers, worst-case time is `O(mq)`, working space is `O(n)`, and total additional space including the output is `O(n + q)`.
- **REVIEW-002 complete:** scoped search and final inspection found one production implementation, no remaining placeholder, no duplicate stale-server rule, no unnecessary abstraction, no changed contract assertion, and no build/dependency change.
- The production method uses only method-local state and does not mutate inputs or shared data. Concurrent valid calls therefore share no production state, while contract version 1 continues to make no explicit concurrency guarantee.
