# Recent Error Tracker traceability

## Contract baseline

- Contract version: 1
- Public boundary: IF-001
- Java operation: API-001
- Work packages: WP-001 through WP-003
- Java compatibility target: 17

## Requirement-to-verification map

| Requirement / criterion | Interface / API | Planned independent verification | Implementation / review |
| --- | --- | --- | --- |
| REQ-001 / AC-001-01, AC-001-02 | IF-001 / API-001 | TEST-004 exact inclusive lower boundary | WP-003 |
| REQ-001 / AC-001-03 | IF-001 / API-001 | TEST-006 duplicate final timestamps with `t = 1` | WP-003 |
| REQ-002 / AC-002-01 | IF-001 / API-001 | TEST-001 problem example | WP-003 |
| REQ-002 / AC-002-02 | IF-001 / API-001 | TEST-002 Sample Case 0 | WP-003 |
| REQ-002 / AC-002-03 | IF-001 / API-001 | TEST-003 Sample Case 1 | WP-003 |
| REQ-002 / AC-002-04 | IF-001 / API-001 | TEST-001 through TEST-004 exact threshold cases | WP-003 |
| REQ-002 / AC-002-05 | IF-001 / API-001 | TEST-001, TEST-006, TEST-007 exact distinct result lists | WP-003 |
| REQ-003 / AC-003-01 | IF-001 / API-001 | TEST-006 and TEST-007 exact natural-order lists | WP-003 |
| REQ-003 / AC-003-02 | IF-001 / API-001 | TEST-007 frequencies and encounter order differ from result order | WP-003 |
| REQ-004 / AC-004-01 | IF-001 / API-001 | TEST-005 asserts a non-null empty list | WP-003 |
| REQ-005 / AC-005-01 | IF-001 / API-001 | TEST-008 single-entry minimum | WP-003 |
| REQ-005 / AC-005-02 | IF-001 / API-001 | TEST-009 100,000 equal timestamps | WP-003 |
| REQ-005 / AC-005-03 | IF-001 / API-001 | REVIEW-001 planned complexity and bounded collections | WP-003 |
| REQ-006 / AC-006-01 | IF-001 / API-001 | BUILD-001 Maven declaration and bindings compile | WP-001, WP-002 |
| REQ-006 / AC-006-02 | IF-001 / API-001 | BUILD-002 `javac --release 17` compatibility check | WP-001, WP-003 |
| REQ-006 / AC-006-03 | IF-001 / API-001 | TEST-010 direct static production binding | WP-001, WP-003 |
| REQ-006 / AC-006-04 | IF-001 / API-001 | REVIEW-002 scoped diff and duplicate search | WP-001–WP-003 |

## Contract test vectors

| Test ID | Inputs | Literal expected result |
| --- | --- | --- |
| TEST-001 | `k=2`, `t=10`, `[100,101,102,105,110]`, `[E1,E2,E1,E1,E2]` | `[E1,E2]` |
| TEST-002 | `k=3`, `t=5`, `[1,2,4,5,6,7,10]`, `[E1,E2,E1,E1,E2,E2,E2]` | `[E2]` |
| TEST-003 | `k=2`, `t=4`, `[1,2,4,5,6]`, `[E1,E2,E1,E1,E2]` | `[E1]` |
| TEST-004 | `k=2`, `t=5`, `[5,5,6,10]`, `[E2,E2,E1,E1]` | `[E1]` |
| TEST-005 | `k=2`, `t=2`, `[1,2,3]`, `[E1,E2,E3]` | `[]` |
| TEST-006 | `k=1`, `t=1`, `[7,8,8]`, `[OLD,B,A]` | `[A,B]` |
| TEST-007 | `k=2`, `t=7`, `[1,2,3,4,5,6,7]`, `[a1,A2,A10,a1,A2,A10,A10]` | `[A10,A2,a1]` |
| TEST-008 | `k=1`, `t=1`, `[1]`, `[E1]` | `[E1]` |
| TEST-009 | 100,000 timestamps all equal to 1, alternating `E2` and `E1`, `k=50,000`, `t=1` | `[E1,E2]` |
| TEST-010 | Invoke the exact static boundary with TEST-001 inputs | `[E1,E2]` |

TEST-009 fixtures are generated as explicitly stated, independent of production behavior. Inputs outside the valid domain, private helpers, validation order, mutation, list mutability, concurrency, timing, and internal data structures have no test IDs because contract version 1 does not specify them. Scale suitability uses an exact maximum input and complexity review without a wall-clock threshold.

## Verification definitions

- **BUILD-001:** Run `mvn -DskipTests compile`, then compile and execute the focused binding appropriate to each later phase.
- **BUILD-002:** Compile the production declaration or implementation with the installed JDK using `javac --release 17`, writing class files only to a temporary directory.
- **REVIEW-001:** Confirm the implementation scans no more than the in-window suffix, counts in `O(u)` storage, sorts only qualifying distinct codes, and has expected `O(w + q log q)` time with `O(u + q)` space.
- **REVIEW-002:** Repeat the scoped behavior search and inspect the final diff for unrelated changes, duplicated rules, unnecessary abstractions, build changes, and remaining placeholders.
- **REVIEW-003:** Confirm every expected result is declared from these contract vectors and neither production calls nor copied production logic serve as a test oracle.

The testing phase implements all scenarios once in `RecentErrorTrackerContract`, with separately declared mock responses in `MockRecentErrorTrackerContract`. The development phase is accepted only through the unchanged direct production binding in `RecentErrorTrackerContractTest`.

## Implemented contract suite

All scenarios and assertions are implemented once in
`src/test/bmv/error_tracker/RecentErrorTrackerContract.java`.

| Test ID | Shared test method |
| --- | --- |
| TEST-001 | `test001ReturnsBothCodesFromTheProblemExample` |
| TEST-002 | `test002ReturnsOnlyE2FromSampleCase0` |
| TEST-003 | `test003ReturnsOnlyE1FromSampleCase1` |
| TEST-004 | `test004IncludesTheLowerBoundaryAndExcludesThePriorSecond` |
| TEST-005 | `test005ReturnsANonNullEmptyListWhenNoCodeQualifies` |
| TEST-006 | `test006IncludesEveryEntryAtTheLatestTimestampAndSortsTheCodes` |
| TEST-007 | `test007UsesNaturalOrderInsteadOfEncounterOrFrequencyOrder` |
| TEST-008 | `test008SupportsTheSingleEntryMinimum` |
| TEST-009 | `test009SupportsOneHundredThousandEntriesAtTheSameTimestamp` |
| TEST-010 | `test010InvokesTheExactPublicOperationWithALiteralCase` |

`src/test/bmv/error_tracker/MockRecentErrorTrackerContract.java` binds the suite
to a Mockito `Operation`. Responses are independently declared in a
scenario-ID-keyed map, and the binding receives no assertion fixture. Its name
intentionally avoids ordinary Surefire `*Test` discovery.

`src/test/bmv/error_tracker/RecentErrorTrackerContractTest.java` binds the
unchanged suite directly to `Result::getErrorCodes`. Development must use this
binding without external doubles or changes to shared assertions.

## Testing-phase evidence

- Command after review repair: `mvn -Dtest=MockRecentErrorTrackerContract test`.
- Result: exit 0; 10 tests run, 0 failures, 0 errors, 0 skipped.
- Report: `target/surefire-reports/bmv.error_tracker.MockRecentErrorTrackerContract.txt`.
- Inspection: TEST-001 through TEST-010 each occur once in the shared suite and once in the separately declared response map.
- Meaning: the run validates contract compilation, discovery, fixtures, assertions, maximum-size fixture construction, and mock mechanics only. Production behavior was not executed and remains unverified.
- Final Java review: no actionable findings within the three testing-phase Java files after strengthening TEST-004 to distinguish correct exclusion of timestamps before the window.

## Production implementation and acceptance

| Requirement | Production implementation | Passing verification |
| --- | --- | --- |
| REQ-001 | Computes `last - t + 1` and walks backward while timestamps remain in the inclusive window | TEST-001 through TEST-004, TEST-006 |
| REQ-002 | Counts in-window codes with one hash-map entry per distinct code and retains counts at least `k` | TEST-001 through TEST-009 |
| REQ-003 | Sorts qualifying distinct codes with Java natural ordering | TEST-001, TEST-006, TEST-007, TEST-009 |
| REQ-004 | Returns the constructed empty list when no map entry qualifies | TEST-005 |
| REQ-005 | Uses one bounded suffix scan and bounded collections at `n = 100,000` | TEST-008, TEST-009, REVIEW-001 |
| REQ-006 | Preserves the required public static operation and uses Java 17-compatible JDK APIs | focused and complete Maven runs, BUILD-002, REVIEW-002 |

- Baseline production command: `mvn -Dtest=RecentErrorTrackerContractTest test` — exit 1; 10 tests run, 0 failures, 10 errors, 0 skipped. Every error was the intentional design placeholder's `UnsupportedOperationException`, confirming the direct binding reached production.
- Focused command after implementation: `mvn -Dtest=RecentErrorTrackerContractTest test` — exit 0; 10 tests run, 0 failures, 0 errors, 0 skipped. Report: `target/surefire-reports/bmv.error_tracker.RecentErrorTrackerContractTest.txt`.
- Java 17 command: `javac --release 17 -d /tmp/error-tracker-java17 src/main/bmv/error_tracker/Result.java` — exit 0.
- Complete command: `mvn test` — exit 0; 129 tests across 18 discovered suites, 0 failures, 0 errors, 0 skipped. Reports: `target/surefire-reports/`.
- `SlowProcessCollectorTest` completed its 20-million-record workload: 1 test, 0 failures, 0 errors, 0 skipped.
- **REVIEW-001 complete:** for `w` in-window entries, `u` distinct in-window codes, and `q` qualifying codes, expected time is `O(w + q log q)` and space is `O(u + q)`.
- **REVIEW-002 complete:** scoped search and final inspection found one production implementation, no remaining placeholder, no duplicate business rule, no unnecessary abstraction, no changed contract assertion, and no build/dependency change.
- **REVIEW-003 complete:** shared expected values remain specification-derived; production logic is not used as an oracle.
