# Longest Common Prefix architecture

## Status

- Stage: 2 — contract version 1
- Design blockers: none
- Production declarations added: none
- Dependency or build changes planned: none

## Reuse findings

Searches found only the required class in `src/main/bmv/prefix/LongestCommonPrefix.java` and its caller in `src/main/bmv/MainBk.java`. No compatible common-prefix implementation or domain abstraction exists elsewhere. The existing class and method signature will be reused, but its explicitly obsolete algorithm is not a source for requirements or expected values. JDK `String` operations are sufficient; no dependency is needed.

## IF-001 — Longest common prefix computation

Traces to REQ-001, REQ-002, REQ-003, and REQ-004.

```java
package bmv.prefix;

public class LongestCommonPrefix {
  public LongestCommonPrefix();

  public String longestCommonPrefix(String[] strs);
}
```

The constructor declaration describes the existing implicit public no-argument constructor. No new production interface is justified: there is one implementation, no collaborator, no alternate production strategy, and the required concrete class is already the public API.

## IF-001 contract version 1

`strs` is valid when it is non-null, has 1–200 elements, every element is non-null and 0–200 characters long, and every nonempty element contains only `a` through `z`.

For valid input the operation returns a non-null string `p` that begins at index zero of every element and has maximal length. If no nonempty common prefix exists, `p` is `""`. Comparison is exact and every valid call completes normally.

Inputs outside this domain have no specified exception, validation order, or returned value. Contract tests will not assert their behavior. Contract version 1 also adds no input-mutation or concurrency guarantee. The operation has no declared collaborator or resource lifecycle, and its result depends only on the supplied valid values.

## Implementation approach

Use a direct vertical scan. Compare each character of the first string at the same position in every remaining string. Return immediately before the first unavailable or unequal character, or return the entire first string when all its characters match.

For `n` strings and shortest-string length `L`, this takes `O(n * L)` character comparisons and `O(1)` auxiliary space, excluding the `O(L)` returned string. Under the stated bounds this is at most 40,000 character comparisons apart from loop overhead.

## Independent contract-test seam

Stage 3 owns one abstract contract suite whose subject creation is replaceable while all scenario inputs and assertions remain shared:

```java
abstract class LongestCommonPrefixContract {
  protected abstract LongestCommonPrefix subjectFor(String scenarioId);
}
```

The abstract suite alone owns each `PrefixCase` with its stable scenario ID, input, and literal requirement-derived expected value. The Stage 3 subclass returns a Mockito mock whose responses are mapped independently by scenario ID and never receives or reads the `PrefixCase` assertion data. The Stage 5 subclass ignores the ID and returns `new LongestCommonPrefix()`. The mock-stage class remains explicitly runnable but is named so the default final Surefire run does not count it as production evidence. The production-backed class uses a discovered name such as `LongestCommonPrefixContractTest`.

The production binding directly constructs and invokes the required concrete public boundary. Neither binding may calculate assertion expectations from production logic, and the mock binding may not replay expected values from the assertion fixture.

## WP-001 — Implement IF-001 / API-001

- Owner: one implementation agent.
- Contract version: 1.
- Requirements: REQ-001, REQ-002, REQ-003, REQ-005.
- Permitted production file: `src/main/bmv/prefix/LongestCommonPrefix.java`.
- Dependencies: frozen contract version 1 and completed Stage 3 suite.
- Reuse: existing class, construction, public method signature, and JDK strings.
- Work: replace the obsolete algorithm with the direct vertical scan and remove unused obsolete implementation details.
- Prohibited scope: tests, artifacts, `pom.xml`, unrelated exercises, or assertion changes.
- Completion: production compiles, public boundary is unchanged, valid-domain rules are implemented, no placeholder remains, duplication searches are clean, and focused checks and handoff evidence are recorded.

One package is appropriate because the algorithm and API occupy one small production file. Splitting it would overlap ownership.

## Design decisions

- **ARCH-DEC-001:** Keep the existing concrete Java class as the sole public boundary.
- **ARCH-DEC-002:** Add no production interface, adapter, factory, or dependency.
- **ARCH-DEC-003:** Leave invalid-input behavior unspecified and untested.
- **ARCH-DEC-004:** Add no mutation or concurrency assertion.
- **ARCH-DEC-005:** Derive expected results only from the requirements.
- **ARCH-DEC-006:** Use one cohesive implementation package.
- **ARCH-DEC-007:** Keep mock responses separate from assertion fixtures; bindings receive only a scenario ID.
