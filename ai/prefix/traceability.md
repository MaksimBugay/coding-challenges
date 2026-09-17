# Longest Common Prefix traceability

## Contract baseline

- Contract version: 1
- Public boundary: IF-001
- Java operation: API-001
- Stage 4 package: WP-001

## Design and planned verification

| Requirement / criterion | Interface / API | Independent verification | Implementation / review |
| --- | --- | --- | --- |
| REQ-001 / AC-001-01 | IF-001 / API-001 | TEST-001: `flower, flow, flight -> fl` | WP-001 |
| REQ-001 / AC-001-02 | IF-001 / API-001 | TEST-002: `dog, racecar, car -> ""` | WP-001 |
| REQ-001 / AC-001-03 | IF-001 / API-001 | TEST-002, TEST-003: distinct first characters | WP-001 |
| REQ-001 / AC-001-04 | IF-001 / API-001 | TEST-004: singleton | WP-001 |
| REQ-001 / AC-001-05 | IF-001 / API-001 | TEST-005: empty element | WP-001 |
| REQ-001 / AC-001-06 | IF-001 / API-001 | TEST-006: shortest element is answer | WP-001 |
| REQ-001 / AC-001-07 | IF-001 / API-001 | TEST-007: first differing index | WP-001 |
| REQ-002 / AC-002-01 | IF-001 / API-001 | TEST-004, TEST-008: one element | WP-001 |
| REQ-002 / AC-002-02 | IF-001 / API-001 | TEST-005: zero-length element | WP-001 |
| REQ-002 / AC-002-03 | IF-001 / API-001 | TEST-009: exactly 200 strings | WP-001 |
| REQ-002 / AC-002-04 | IF-001 / API-001 | TEST-008, TEST-009: length 200 | WP-001 |
| REQ-002 / AC-002-05 | IF-001 / API-001 | TEST-009: maximum boundary | WP-001 |
| REQ-003 / AC-003-01 | IF-001 / API-001 | TEST-010: production binding directly constructs/invokes public API with `["same", "sample"] -> "sam"` | WP-001; BUILD-001 |
| REQ-003 / AC-003-02 | IF-001 / API-001 | BUILD-001: Maven compiles `MainBk` | WP-001 |
| REQ-004 / AC-004-01 | IF-001 / API-001 | REVIEW-001: independent oracle review | Test agent; verification agent |
| REQ-005 / AC-005-01 | IF-001 / API-001 | BUILD-001: focused and complete Maven suites | WP-001; Stage 5/6 |
| REQ-005 / AC-005-02 | IF-001 / API-001 | REVIEW-002: scoped final diff | WP-001; Stage 6 |
| REQ-005 / AC-005-03 | IF-001 / API-001 | REVIEW-003: no build/dependency change | WP-001; Stage 6 |

## Contract test scenarios

| Test ID | Input | Expected |
| --- | --- | --- |
| TEST-001 | `["flower", "flow", "flight"]` | `"fl"` |
| TEST-002 | `["dog", "racecar", "car"]` | `""` |
| TEST-003 | `["a", "b"]` | `""` |
| TEST-004 | `["prefix"]` | `"prefix"` |
| TEST-005 | `["abc", "", "ab"]` | `""` |
| TEST-006 | `["inter", "internet", "internal"]` | `"inter"` |
| TEST-007 | `["abc", "abd", "abz"]` | `"ab"` |
| TEST-008 | one string of exactly 200 `a` characters | same 200 characters |
| TEST-009 | 200 strings each containing exactly 200 `a` characters | 200 `a` characters |
| TEST-010 | production binding directly constructs and calls the concrete boundary with `["same", "sample"]` | `"sam"` |

Inputs outside the valid domain, mutation, concurrency, private helpers, and algorithm structure have no test IDs because they are outside contract version 1.

TEST-001 through TEST-010 are implemented once in `src/test/bmv/prefix/LongestCommonPrefixContract.java`. Stage 3 binds them through `MockLongestCommonPrefixContract`, whose independently declared response map receives only scenario IDs.

Stage 5 binds the unchanged suite through `src/test/bmv/prefix/LongestCommonPrefixContractTest.java`. Its factory directly returns `new LongestCommonPrefix()`. Production behavior for IF-001/API-001 is implemented in `src/main/bmv/prefix/LongestCommonPrefix.java`.

## Verification methods

- **BUILD-001:** Compile and execute the focused production-backed contract test, then the complete required Maven suite.
- **REVIEW-001:** Confirm test expectations are literal and requirement-derived, with no production-derived oracle.
- **REVIEW-002:** Inspect the final diff for scope and accidental changes.
- **REVIEW-003:** Confirm no dependency/build changes.
- **REVIEW-004:** Search before and after implementation for duplicate common-prefix logic and explain intentional separation.

## Stage 3 evidence

- Command: `mvn -Dtest=MockLongestCommonPrefixContract test`
- Result: exit 0; 10 tests run, 0 failures, 0 errors, 0 skipped.
- Report: `target/surefire-reports/bmv.prefix.MockLongestCommonPrefixContract.txt`.
- Meaning: validates contract-suite discovery and mock binding only; it is not production correctness evidence.

## Production acceptance evidence

- Focused command: `mvn -Dtest=LongestCommonPrefixContractTest test`
- Focused result: exit 0; 10 tests run, 0 failures, 0 errors, 0 skipped.
- Focused report: `target/surefire-reports/bmv.prefix.LongestCommonPrefixContractTest.txt`.
- Complete command: `mvn test`
- Complete result: exit 0; 55 tests run across 10 discovered suites, 0 failures, 0 errors, 0 skipped.
- Complete reports: `target/surefire-reports`.
- `SlowProcessCollectorTest`: 1 test run, 0 failures, 0 errors, 0 skipped; its 20-million-record workload completed.
- Bytecode inspection confirmed the production binding executes `new LongestCommonPrefix()` through its no-argument constructor.
- REVIEW-001 through REVIEW-004 passed: the oracle is requirement-derived; scope is limited; no build/dependency change occurred; and no duplicate production prefix algorithm exists.
