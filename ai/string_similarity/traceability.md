# Traceability — String Similarity

| Requirement | Acceptance Criterion | Interface/API | Verification | Work Package |
| --- | --- | --- | --- | --- |
| REQ-001 | AC-001-01 (`"ababaa"` → 11) | API-001 / IF-001 | `ResultTest.stringSimilarity_sample1_returnsElevenSample()` | WP-002, WP-003 |
| REQ-001 | AC-001-02 (`"aa"` → 3) | API-001 / IF-001 | `ResultTest.stringSimilarity_sample2_returnsThree()` | WP-002, WP-003 |
| REQ-001 | AC-001-03 (`"a"` → 1) | API-001 / IF-001 | `ResultTest.stringSimilarity_singleCharacter_returnsOne()` | WP-002, WP-003 |
| REQ-001 | AC-001-04 (`"aaaa"` → 10) | API-001 / IF-001 | `ResultTest.stringSimilarity_allIdenticalCharacters_returnsTriangularSum()` | WP-002, WP-003 |
| REQ-001 | AC-001-05 (`"abcde"` → 5) | API-001 / IF-001 | `ResultTest.stringSimilarity_noSharedPrefixes_returnsLengthOnly()` | WP-002, WP-003 |
| REQ-001 | AC-001-06 (`n=20000` all-`'a'` → 200010000, ≤2s) | API-001 / IF-001 | `ResultTest.stringSimilarity_largeIdenticalInput_completesWithinTimeBound()` | WP-002, WP-003 |
| REQ-002 | AC-002-01 (I/O order) | IF-002 (HackerRank stdin/stdout adapter, unmodified) | Verified only via HackerRank Run Code during the entry phase, not local JUnit (see architecture.md IF-002) | (browser entry phase, post-development) |

No requirement lacks a verification path. REQ-002's adapter is
intentionally unmodified HackerRank boilerplate and is out of scope for
local JUnit coverage; its verification is the browser Run Code step
described in the `hackerrank-practice` skill.

## Implementation status (development phase)

`src/main/bmv/string_similarity/Result.java` implements IF-001 with the
Z-array algorithm (`stringSimilarity`). All six `ResultTest` cases
(TEST-001..TEST-006, covering AC-001-01..06) pass against this production
binding — `mvn -Dtest=ResultTest test`: 6/6 green. Full-suite acceptance
(`mvn test`, 115 tests including `SlowProcessCollectorTest`): all green.
AC-002-01 remains unverified locally by design (browser entry phase only).
