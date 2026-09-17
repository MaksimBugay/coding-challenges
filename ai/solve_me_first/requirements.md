# Solve Me First requirements

## Status and sources

- Design stage: complete
- Contract version: 1
- Blocking questions: none
- Authoritative challenge: `src/main/bmv/solve_me_first/task.md`
- Supporting project sources: `AGENTS.md`, `README.md`, and `pom.xml`
- HackerRank language: Java 15, parsed from the page

Repository search found no existing `Solve Me First` package, handoff, function, or equivalent task-specific implementation to reuse.

## REQ-001 — Return the sum

For every valid `a` and `b`, `solveMeFirst(a, b)` returns their mathematical sum.

- **AC-001-01:** `solveMeFirst(2, 3)` returns `5`.
- **AC-001-02:** `solveMeFirst(7, 3)` returns `10`.
- **AC-001-03:** `solveMeFirst(1, 1)` returns `2`.
- **AC-001-04:** `solveMeFirst(1000, 1000)` returns `2000`.

## REQ-002 — Support the stated domain

Both arguments are integers from 1 through 1000 inclusive. Inputs outside this range have no specified behavior, validation, or exception contract.

- **AC-002-01:** Both inclusive boundaries work.
- **AC-002-02:** Every valid result fits in Java `int`.

## REQ-003 — Preserve the HackerRank contract

The editor-ready submission contains `public class Solution` and `static int solveMeFirst(int a, int b)`. Its `main` reads two integers and prints the result.

- **AC-003-01:** The editor-ready source compiles with Java 15 syntax.
- **AC-003-02:** Input `2 3` prints `5` followed by a line terminator.

## REQ-004 — Keep the local solution scoped and build-compatible

The reusable local boundary remains in package `bmv.solve_me_first`, needs no dependency, and compiles under the configured JDK 25 Maven build while using Java syntax supported by HackerRank Java 15.

- **AC-004-01:** Focused tests pass against production code.
- **AC-004-02:** The Maven project compiles without build or dependency changes.
- **AC-004-03:** Unrelated exercises and existing user changes remain untouched.

## Scope decisions

- Invalid inputs, overflow outside the stated domain, concurrency, persistence, networking, and mutation policies are outside the challenge.
- The operation is stateless and has no resource lifecycle.
- No dependency or abstraction beyond one small concrete utility class is justified.
