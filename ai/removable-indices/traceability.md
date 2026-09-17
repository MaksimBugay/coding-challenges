# Removable Indices traceability

## Contract baseline

- Contract version: 1
- Public boundary: IF-001
- Java operation: API-001
- Work packages: WP-001 through WP-003

## Requirement-to-verification map

| Requirement / criterion | Interface / API | Planned independent verification | Implementation / review |
| --- | --- | --- | --- |
| REQ-001 / AC-001-01 | IF-001 / API-001 | TEST-001 required repeated-run example | WP-003 |
| REQ-001 / AC-001-02 | IF-001 / API-001 | TEST-002 unique middle removal | WP-003 |
| REQ-001 / AC-001-03 | IF-001 / API-001 | TEST-001, TEST-003, TEST-004 repeated runs | WP-003 |
| REQ-001 / AC-001-04 | IF-001 / API-001 | TEST-003 beginning; TEST-004 and TEST-005 end | WP-003 |
| REQ-001 / AC-001-05 | IF-001 / API-001 | TEST-001 through TEST-007 literal equality cases; REVIEW-001 oracle independence | WP-003 |
| REQ-002 / AC-002-01 | IF-001 / API-001 | TEST-001, TEST-003, TEST-004 exact ordered lists | WP-003 |
| REQ-002 / AC-002-02 | IF-001 / API-001 | TEST-001, TEST-003, TEST-004 exact lists with unique elements | WP-003 |
| REQ-003 / AC-003-01 | IF-001 / API-001 | TEST-007 incompatible strings return `[-1]` | WP-003 |
| REQ-003 / AC-003-02 | IF-001 / API-001 | TEST-001 through TEST-007 exact-list assertions | WP-003 |
| REQ-004 / AC-004-01 | IF-001 / API-001 | TEST-006 minimum valid lengths | WP-003 |
| REQ-004 / AC-004-02 | IF-001 / API-001 | TEST-008 maximum `str1` length with final removal | WP-003 |
| REQ-004 / AC-004-03 | IF-001 / API-001 | TEST-009 maximum all-removable result | WP-003 |
| REQ-004 / AC-004-04 | IF-001 / API-001 | REVIEW-002 two-scan complexity and no per-index candidate construction | WP-003 |
| REQ-005 / AC-005-01 | IF-001 / API-001 | BUILD-001 declaration and both bindings compile | WP-001, WP-002 |
| REQ-005 / AC-005-02 | IF-001 / API-001 | TEST-010 direct static production binding | WP-001, WP-003 |
| REQ-005 / AC-005-03 | IF-001 / API-001 | REVIEW-003 unchanged build/dependencies | WP-001–WP-003 |
| REQ-005 / AC-005-04 | IF-001 / API-001 | REVIEW-004 scoped diff and duplicate search | WP-001–WP-003 |

## Contract test vectors

| Test ID | Input | Literal expected result |
| --- | --- | --- |
| TEST-001 | `"abdgggda"`, `"abdggda"` | `[3, 4, 5]` |
| TEST-002 | `"abc"`, `"ac"` | `[1]` |
| TEST-003 | `"aabc"`, `"abc"` | `[0, 1]` |
| TEST-004 | `"abcc"`, `"abc"` | `[2, 3]` |
| TEST-005 | `"abcd"`, `"abc"` | `[3]` |
| TEST-006 | `"aa"`, `"a"` | `[0, 1]` |
| TEST-007 | `"abc"`, `"de"` | `[-1]` |
| TEST-008 | `"a".repeat(199_999) + "b"`, `"a".repeat(199_999)` | `[199_999]` |
| TEST-009 | `"a".repeat(200_000)`, `"a".repeat(199_999)` | integers `0` through `199_999`, in order |
| TEST-010 | Invoke the exact static boundary with `"xyyz"`, `"xyz"` | `[1, 2]` |

TEST-009's expected list is generated in the test as the mathematical integer range `0..199_999`; it is specified here independently of production behavior. Inputs outside the valid domain, private helpers, validation order, and exact internal code structure have no test IDs because contract version 1 does not specify them. Scale suitability uses exact boundary inputs and complexity review, without a wall-clock threshold.

## Verification definitions

- **BUILD-001:** Run `mvn -DskipTests compile`, then compile and execute the focused binding appropriate to each later phase.
- **REVIEW-001:** Confirm all expected results come from the contract vectors and no production call or copied production algorithm acts as an oracle.
- **REVIEW-002:** Confirm at most two linear scans plus output enumeration, `O(n)` time, `O(1)` auxiliary working space, and `O(k)` result space.
- **REVIEW-003:** Confirm `pom.xml`, dependencies, Mockito agent configuration, and source roots are unchanged.
- **REVIEW-004:** Repeat the scoped behavior search and inspect the final diff for unrelated changes, duplicated rules, unnecessary abstractions, and remaining placeholders.

The testing phase implements all scenarios once in `RemovableIndicesContract`, with separately declared mock responses in `MockRemovableIndicesContract`. The development phase is accepted only through the unchanged suite's direct production binding in `RemovableIndicesContractTest`.

## Implemented contract suite

All scenarios and assertions are implemented once in `src/test/bmv/removable_indices/RemovableIndicesContract.java`.

| Test ID | Shared test method |
| --- | --- |
| TEST-001 | `test001ReturnsEveryIndexInTheRequiredRepeatedRunExample` |
| TEST-002 | `test002ReturnsTheUniqueMiddleRemoval` |
| TEST-003 | `test003ReturnsRepeatedRemovalsAtTheBeginningInOrder` |
| TEST-004 | `test004ReturnsRepeatedRemovalsAtTheEndInOrder` |
| TEST-005 | `test005RemovesTheFinalCharacter` |
| TEST-006 | `test006SupportsMinimumLengthsWithEveryPositionRemovable` |
| TEST-007 | `test007ReturnsOnlyMinusOneWhenRemovalIsImpossible` |
| TEST-008 | `test008SupportsTheMaximumInputLengthWithOneRemoval` |
| TEST-009 | `test009ReturnsEveryIndexAtTheMaximumResultSize` |
| TEST-010 | `test010InvokesTheExactPublicOperationWithALiteralCase` |

`src/test/bmv/removable_indices/MockRemovableIndicesContract.java` binds the suite to a Mockito `Operation`. Responses are independently declared in a scenario-ID-keyed map, and the binding receives no assertion fixture. Its name intentionally avoids ordinary Surefire `*Test` discovery.

`src/test/bmv/removable_indices/RemovableIndicesContractTest.java` binds the unchanged suite directly to `Result::getRemovableIndices`. Development must use this binding without external doubles or changes to shared assertions.

## Testing-phase evidence

- Command: `mvn -Dtest=MockRemovableIndicesContract test`
- Result: exit 0; 10 tests run, 0 failures, 0 errors, 0 skipped.
- Report: `target/surefire-reports/bmv.removable_indices.MockRemovableIndicesContract.txt`.
- Inspection: TEST-001 through TEST-010 each occur once in the shared suite and once in the separately declared response map.
- Meaning: the run validates contract compilation, discovery, fixtures, assertions, maximum-size fixture construction, and mock mechanics only. Production behavior was not executed and remains unverified.
- Required Java review: no actionable findings within the three testing-phase Java files. Requirements/correctness, Java 25 compatibility, SOLID/minimality, reuse/duplication, performance, thread safety, and tests were checked. Dependency freshness/security was not applicable because this phase changed no dependency.

## Production implementation and acceptance

| Requirement | Production implementation | Passing verification |
| --- | --- | --- |
| REQ-001 | `Result.getRemovableIndices`: matching-prefix upper boundary, shifted-suffix lower boundary, and inclusive overlap enumeration | TEST-001 through TEST-006, TEST-010 |
| REQ-002 | Ascending integer loop over the valid inclusive interval | TEST-001, TEST-003, TEST-004, TEST-006, TEST-009, TEST-010 |
| REQ-003 | Empty interval returns `List.of(-1)` exclusively | TEST-007 |
| REQ-004 | Two bounded scans and output enumeration; no candidate strings | TEST-006, TEST-008, TEST-009, REVIEW-002 |
| REQ-005 | Public static `bmv.removable_indices.Result.getRemovableIndices(String, String)` using only JDK collections | focused and complete Maven runs, REVIEW-003, REVIEW-004 |

- Baseline production command: `mvn -Dtest=RemovableIndicesContractTest test` — exit 1; 10 tests run, 0 failures, 10 errors, 0 skipped. Every error was the intentional design placeholder's `UnsupportedOperationException`, confirming the direct binding reached production.
- Focused command after implementation: `mvn -Dtest=RemovableIndicesContractTest test` — exit 0; 10 tests run, 0 failures, 0 errors, 0 skipped. Report: `target/surefire-reports/bmv.removable_indices.RemovableIndicesContractTest.txt`.
- Complete command: `mvn test` — exit 0; 77 tests across 12 discovered suites, 0 failures, 0 errors, 0 skipped. Reports: `target/surefire-reports/`.
- `SlowProcessCollectorTest` completed its 20-million-record workload: 1 test, 0 failures, 0 errors, 0 skipped.
- **REVIEW-001 complete:** shared expected values remain specification-derived; production logic is not used as an oracle.
- **REVIEW-002 complete:** worst-case time is `O(n + k)`, hence `O(n)` because `k <= n`; auxiliary working space is `O(1)` and required result space is `O(k)`.
- **REVIEW-003 complete:** `pom.xml`, dependencies, Mockito agent configuration, and source roots are unchanged.
- **REVIEW-004 complete:** scoped search and final inspection found one production implementation, no remaining placeholder, no duplicate business rule, no unnecessary abstraction, and no unrelated exercise edit.
