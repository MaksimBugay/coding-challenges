# Bead Ornaments requirements

Status: design complete  
Contract: `bead-ornaments/v1.0.0`  
Language: Java release 15, HackerRank label **Java 15**, resolution source `user`

## Source inventory

- `src/main/bmv/bead_ornaments/task.md`: authoritative captured HackerRank
  statement, input format, constraints, examples, starter code, and analysis.
- HackerRank `https://www.hackerrank.com/challenges/beadornaments/problem`: source
  page selected in Playwright tab 0.
- `README.md`, `pom.xml`, and root `AGENTS.md`: project build and development
  rules.
- No statement images or existing implementation/tests were found.

## Requirements and acceptance criteria

### REQ-001 — Count distinct ornament trees

Given the positive bead counts for 1 to 10 colors, return the number of distinct
undirected labeled trees constructible by the statement algorithm.

- AC-001-01: `[2, 1]` returns `2`.
- AC-001-02: `[2, 2]` returns `4`.
- AC-001-03: `[4]` returns `16`.
- AC-001-04: `[3, 1]` returns `9`.
- AC-001-05: `[1, 1, 1, 1, 1]` returns `125`.

Source: `task.md` problem statement and sample.

### REQ-002 — Preserve the Java function contract

Expose `public static int beadOrnaments(List<Integer> b)` through class `Result`.
The input list contains the number of beads for each color.

- AC-002-01: Code using the captured Java 15 signature compiles.
- AC-002-02: The method does not mutate the supplied list.

Source: `task.md` function description and Java 15 starter.

### REQ-003 — Apply the required modulus

Return the mathematical count modulo `1_000_000_007` as an `int`.

- AC-003-01: `[30, 30, 30, 30, 30, 30, 30, 30, 30, 30]` returns the
  independently calculated modular result `409191278`.

Source: `task.md` return contract and constraints. The expected value will be
independently rechecked in the test phase.

### REQ-004 — Handle contractual boundaries

All stated inputs have 1 to 10 elements, and each value is 1 to 30.

- AC-004-01: `[1]` returns `1`.
- AC-004-02: `[30]` returns `30^28 mod 1_000_000_007`.
- AC-004-03: Ten one-bead colors return `10^8 mod 1_000_000_007`.

Source: `task.md` constraints and tree interpretation.

## Scope and exclusions

The scope is the HackerRank function and a separate editor-ready Java 15 file.
Parsing, file output, and browser submission are outside the reusable algorithm.
The starter's `Solution.main` is retained only in the editor-ready adapter.
Invalid, null, empty, or out-of-range inputs are outside the challenge contract,
so no exception behavior is specified.

## Decisions and assumptions

- The observable object is an undirected edge set, as stated by HackerRank.
- The input list is read-only because no mutation is needed.
- The function is stateless and safe for concurrent calls when callers do not
  mutate a shared input list during a call.
- The page provides no separate output-format text beyond returning/printing one
  result for each case.

There are no unresolved correctness questions.
