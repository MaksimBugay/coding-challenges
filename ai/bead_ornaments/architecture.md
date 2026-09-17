# Bead Ornaments architecture

Contract: `bead-ornaments/v1.0.0`

## Reuse findings

Repository searches for `beadOrnaments`, `Bead Ornaments`, `beadornaments`, and
related package names found no existing implementation or tests. The design uses
only `java.util.List` and primitive modular arithmetic. No dependency or build
change is needed.

## Boundary IF-001

Declaration: `src/main/bmv/bead_ornaments/Result.java`

```java
public static int beadOrnaments(List<Integer> b)
```

The method accepts a challenge-valid, caller-owned list, returns the count modulo
`1_000_000_007`, does not mutate input, performs no I/O, and has no shared state.
Invalid-input behavior is unspecified. The design-phase body is an explicit
fail-fast placeholder which WP-DEV-001 must remove.

## Algorithm contract

For one color of size `m`, return `1` when `m == 1`; otherwise return
`m^(m-2) mod M` by Cayley's formula. For `n > 1`, return

```text
(sum(b))^(n-2) * product(b[i]^(b[i]-1)) mod M
```

using binary modular exponentiation and `long` intermediates. This is
`O(n log S)` time and `O(1)` auxiliary space, where `S = sum(b) <= 300`.

## Test construction seam

`src/test/bmv/bead_ornaments/BeadOrnamentsContractTest.java` will be an abstract
black-box suite whose only seam is:

```java
protected abstract int beadOrnaments(List<Integer> beadCounts);
```

`MockBeadOrnamentsContractTest` supplies an independently programmed deterministic
fixture during testing. `ResultBeadOrnamentsContractTest` supplies
`Result::beadOrnaments` during development. Both bindings inherit the same
scenario methods and assertions.

## Work packages

### WP-DES-001 — Public declaration (design owner)

- Files: `Result.java` and `ai/bead_ornaments/*`.
- Requirements: REQ-001 through REQ-004.
- Complete when documents agree at v1.0.0 and declarations compile.

### WP-TEST-001 — Independent contract suite (testing owner)

- Files: contract suite and mock binding under `src/test/bmv/bead_ornaments/`.
- Dependencies: WP-DES-001.
- Derive expected values from `task.md` and independent arithmetic, without
  calling production code.
- Complete when every AC maps to a scenario and the mock-stage suite passes.

### WP-DEV-001 — Production algorithm and adapter (development owner)

- Files: `Result.java`, production test binding, and
  `HackerRankSolution.java` in the task package; traceability/log updates.
- Dependencies: WP-TEST-001 and its review.
- Remove the placeholder, implement the stated formula and modular power, bind
  the unchanged contract suite, and create the exact Java 15 editor source.
- Complete when focused and full Maven suites pass and no placeholder remains.

No packages overlap in time; the phases execute serially.

## Commands

- Design compile: `mvn -DskipTests compile`
- Mock contract tests: `mvn -Dtest=MockBeadOrnamentsContractTest test`
- Production contract tests: `mvn -Dtest=ResultBeadOrnamentsContractTest test`
- Final acceptance: `mvn test`
