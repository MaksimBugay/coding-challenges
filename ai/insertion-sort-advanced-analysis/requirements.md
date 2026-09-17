# Insertion Sort Advanced Analysis requirements

## Source inventory

- `src/main/bmv/insertion_sort_advanced_analysis/task.md`: authoritative capture of the open HackerRank page, including Java 15 starter code.
- HackerRank page: `https://www.hackerrank.com/challenges/insertion-sort/problem?isFullScreen=true`.
- No statement image is required.

## Requirements

- **REQ-001:** `Result.insertionSort(List<Integer>)` returns the insertion-sort shift count, equivalently the count of pairs `(i, j)` where `i < j` and `arr[i] > arr[j]`. Source: statement and example. **AC-001-01:** `[2,1,3,1,2]` returns `4`. **AC-001-02:** `[4,3,2,1]` returns `6`.
- **REQ-002:** Equal values do not cause shifts. Source: strict ordering in insertion sort and sample. **AC-002-01:** `[1,1,1,2,2]` returns `0`. **AC-002-02:** `[2,2,1,1]` returns `4`.
- **REQ-003:** Support `1 <= n <= 100000` and values `1..10000000` within practical challenge limits. **AC-003-01:** a 100,000-element ordered list completes and returns `0`; static analysis establishes `O(n log n)` time.
- **REQ-004:** Preserve the exact Java 15 operation signature and `int` result required by HackerRank. **AC-004-01:** a reverse list large enough to exceed `Integer.MAX_VALUE` returns the Java narrowing result of the mathematical count.
- **REQ-005:** The operation does not mutate the caller-owned list. **AC-005-01:** the input remains value-equal to a saved copy after a call.

## Scope and decisions

Valid inputs follow the published constraints. Nulls, null elements, empty lists, and out-of-range values are unspecified. The blank Output Format section is supplemented only by the provided driver's observable one-result-per-line behavior. Contract version 1 follows the starter's `int` API despite the documented overflow conflict. No I/O entry point is needed in the reusable local package; the editor-ready source supplies HackerRank's driver.
