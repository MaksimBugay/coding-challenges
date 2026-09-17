# Solve Me First architecture

## Contract version 1

### IF-001 — Local reusable boundary

```java
package bmv.solve_me_first;

public final class SolveMeFirst {
  public static int solveMeFirst(int a, int b);
}
```

The design phase supplies a fail-fast body only so the declaration compiles. Development must replace it with the production behavior.

### IF-002 — HackerRank adapter

The editor-ready artifact will contain the captured `public class Solution`, its required package-private static method, and the supplied standard-input/output `main`. It delegates the required behavior to the same one-expression algorithm because HackerRank accepts a standalone source file.

## Design and reuse

No matching implementation exists in the repository. Java integer addition is the complete algorithm; no interface, factory, collaborator, dependency, or validation layer is needed. For all valid inputs, returning `a + b` takes `O(1)` time and `O(1)` auxiliary space.

## Independent test seam

One abstract contract suite owns literal inputs and expected values and calls a `java.util.function.IntBinaryOperator` supplied by `subjectFor(scenarioId)`. A mock-stage binding maps scenario IDs to independently declared literal responses. The production binding returns `SolveMeFirst::solveMeFirst` without changing shared assertions.

## Work packages

- **WP-001, testing:** own `src/test/bmv/solve_me_first/SolveMeFirstContract.java` and `MockSolveMeFirstContract.java`; cover REQ-001 through REQ-004 with mock-stage evidence.
- **WP-002, development:** own `src/main/bmv/solve_me_first/SolveMeFirst.java`, `src/main/bmv/solve_me_first/Solution.java.txt`, and the production test binding; replace the placeholder, create the exact standalone adapter, and run focused plus full acceptance.

Both packages use contract version 1. No dependency or `pom.xml` change is permitted or needed.
