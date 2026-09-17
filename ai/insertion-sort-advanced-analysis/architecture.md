# Insertion Sort Advanced Analysis architecture

## Status

- Phase: development complete
- Contract version: 1
- Blockers: none
- Java on HackerRank: Java 15, resolved from the page

## Reuse findings

Repository searches found no equivalent inversion-count implementation, declaration, caller, or test. The challenge requires the candidate algorithm. JDK arrays and `List` are sufficient; no dependency or build change is needed.

## IF-001 — shift-count operation, contract version 1

```java
package bmv.insertion_sort_advanced_analysis;

public final class Result {
    public static int insertionSort(List<Integer> arr);
}
```

For valid inputs, the result is `(int) inversions`, where `inversions` is the mathematical number of index pairs `i < j` with `arr[i] > arr[j]`. The method does not mutate `arr`, performs no I/O, and uses no shared mutable state. Invalid-input behavior is unspecified.

The planned implementation copies list values into an `int[]`, counts inversions with merge sort using a reusable buffer and `long` accumulation, then narrows once to the required `int`. It runs in `O(n log n)` time and `O(n)` auxiliary heap space with `O(log n)` stack depth.

## Independent test seam

The testing phase owns an abstract shared `InsertionSortContract` whose `operationFor(scenarioId)` returns a small functional operation. `MockInsertionSortContract` supplies independently declared responses through Mockito; `InsertionSortContractTest` binds unchanged scenarios to `Result::insertionSort` during development. The shared suite covers the singleton boundary, duplicates, both published examples, starter-required overflow narrowing, the maximum ordered size, and non-mutation.

Planned paths:

- `src/test/bmv/insertion_sort_advanced_analysis/InsertionSortContract.java`
- `src/test/bmv/insertion_sort_advanced_analysis/MockInsertionSortContract.java`
- `src/test/bmv/insertion_sort_advanced_analysis/InsertionSortContractTest.java`
- `src/main/bmv/insertion_sort_advanced_analysis/Solution.java.txt` for editor-ready standalone source after acceptance

## Work packages

- **WP-001 (design):** own `Result.java` and the handoff files; provide a compiling fail-fast declaration.
- **WP-002 (testing):** complete. The three owned test files implement TEST-001 through TEST-008 without production logic. Mock validation passes; production remains the intentional design placeholder.
- **WP-003 (development):** complete. Replaced the placeholder in `Result.java`, accepted the unchanged production binding, created `Solution.java.txt`, and passed focused, Java 15 compilation, and complete-suite validation.

The phases run serially and do not edit shared files concurrently.
