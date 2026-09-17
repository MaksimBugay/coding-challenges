# Solve Me First traceability

## Contract baseline

- Contract version: 1
- Interfaces: IF-001 and IF-002
- APIs: API-001 and API-002

| Requirement / criterion | Boundary | Planned verification | Work package |
| --- | --- | --- | --- |
| REQ-001 / AC-001-01 | IF-001 / API-001 | TEST-001: `2 + 3 = 5` | WP-001, WP-002 |
| REQ-001 / AC-001-02 | IF-001 / API-001 | TEST-002: `7 + 3 = 10` | WP-001, WP-002 |
| REQ-001 / AC-001-03 | IF-001 / API-001 | TEST-003: minimum pair | WP-001, WP-002 |
| REQ-001 / AC-001-04 | IF-001 / API-001 | TEST-004: maximum pair | WP-001, WP-002 |
| REQ-002 / AC-002-01 | IF-001 / API-001 | TEST-003 and TEST-004 | WP-001, WP-002 |
| REQ-002 / AC-002-02 | IF-001 / API-001 | REVIEW-001: result bound `2000` | WP-002 |
| REQ-003 / AC-003-01 | IF-002 / API-002 | BUILD-002: compile editor-ready source with Java 15 compatibility | WP-002 |
| REQ-003 / AC-003-02 | IF-002 / API-002 | RUN-001: execute standalone input/output `2 3 -> 5` | WP-002 |
| REQ-004 / AC-004-01 | IF-001 | BUILD-001: focused production contract suite | WP-002 |
| REQ-004 / AC-004-02 | IF-001 | BUILD-003: Maven compile and full suite | WP-002 |
| REQ-004 / AC-004-03 | Both | REVIEW-002: scoped diff and duplication search | WP-002 |

TEST-001 through TEST-004 are implemented once in `src/test/bmv/solve_me_first/SolveMeFirstContract.java`. `MockSolveMeFirstContract` supplies independently declared responses by scenario ID. `SolveMeFirstContractTest` returns `SolveMeFirst::solveMeFirst` and runs the same assertions against production.

## Verification evidence

- Mock stage: `mvn -Dtest=MockSolveMeFirstContract test` passed 4/4; this validates test mechanics only.
- BUILD-001: `mvn -Dtest=SolveMeFirstContractTest test` passed 4/4 against production.
- BUILD-002 and RUN-001: the editor artifact compiled with `javac --release 15`; input `2 3` produced `5`.
- BUILD-003: `mvn test` passed 101/101 across the complete repository suite, including `SlowProcessCollectorTest`.
- REVIEW-001: the maximum valid sum is `2000`, within `int`.
- REVIEW-002: final searches found only the intentional reusable implementation and standalone HackerRank adapter; no placeholder, dependency change, or unrelated edit was introduced.
