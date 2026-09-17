# Recent Error Tracker requirements

## Status

- Phase: solution design complete
- Contract version: 1
- Raw requirements folder: `src/main/bmv/error_tracker/`
- Java compatibility target: 17
- Correctness-blocking questions: none
- Missing or unreadable source material: none

## Source inventory

| Source ID | Path and section | Role |
| --- | --- | --- |
| SRC-001 | `src/main/bmv/error_tracker/task-img1.png`, problem statement and worked example | Authoritative log model, inclusive time-window formula, frequency rule, result ordering, empty result, and example |
| SRC-002 | `src/main/bmv/error_tracker/task-img2.jpg`, constraints and Sample Case 0 input | Authoritative valid input limits and first sample inputs |
| SRC-003 | `src/main/bmv/error_tracker/task-img3.jpg`, Sample Case 0 output and explanation | Authoritative first sample result and window counts |
| SRC-004 | `src/main/bmv/error_tracker/task-img4.jpg`, Sample Case 1 and Java starter | Authoritative second sample, operation name, parameter order/types, and return type |
| SRC-005 | `README.md` and `pom.xml` | Project source roots, Maven build, dependencies, and repository-wide Java 25 setting |
| SRC-006 | `AGENTS.md` | Project-wide scope, design, reuse, and verification rules |
| SRC-007 | User instruction in this chat | Authoritative Java 17 compatibility target for this exercise |
| SRC-008 | Repository implementation, caller, test, and handoff search | No existing error-tracker implementation, caller, test, or task handoff exists |

The raw requirements folder contains exactly four readable 1280-by-598 image files and no nested or referenced material. The screenshots show a hosted Java 17 editor and generated input/output code; only the declared `Result.getErrorCodes` operation is part of the required business boundary.

## Terminology

Let `n` be the number of log entries and `lastTimestamp = timestamps[n - 1]`. The recent window is the inclusive integer interval:

```text
[lastTimestamp - t + 1, lastTimestamp]
```

An occurrence is in the window exactly when its corresponding timestamp belongs to that interval.

## Functional requirements

### REQ-001 — Select entries from the recent time window

Use only entries whose timestamps fall within the inclusive recent window anchored at the final timestamp.

Sources: SRC-001, SRC-003, SRC-004.

- **AC-001-01:** An entry at `lastTimestamp - t + 1` is included.
- **AC-001-02:** An entry one second before the lower boundary is excluded.
- **AC-001-03:** Every entry at `lastTimestamp`, including entries sharing that timestamp, is included.

### REQ-002 — Return codes meeting the frequency threshold

Return each distinct error code that occurs at least `k` times within the recent window.

Sources: SRC-001, SRC-003, SRC-004.

- **AC-002-01:** With `k = 2`, `t = 10`, timestamps `[100, 101, 102, 105, 110]`, and codes `["E1", "E2", "E1", "E1", "E2"]`, return `["E1", "E2"]` because both codes occur twice in `[101, 110]`.
- **AC-002-02:** With Sample Case 0 (`k = 3`, `t = 5`, timestamps `[1, 2, 4, 5, 6, 7, 10]`, codes `["E1", "E2", "E1", "E1", "E2", "E2", "E2"]`), return `["E2"]`.
- **AC-002-03:** With Sample Case 1 (`k = 2`, `t = 4`, timestamps `[1, 2, 4, 5, 6]`, codes `["E1", "E2", "E1", "E1", "E2"]`), return `["E1"]`.
- **AC-002-04:** A code occurring exactly `k` times qualifies; a code occurring `k - 1` times does not.
- **AC-002-05:** A qualifying code appears once in the result regardless of its occurrence count.

### REQ-003 — Sort the result lexicographically

Return qualifying codes in ascending lexicographic order.

Source: SRC-001.

- **AC-003-01:** Multiple qualifying codes are returned in Java `String` natural order.
- **AC-003-02:** Result order does not depend on first occurrence, last occurrence, or frequency.

### REQ-004 — Return an empty list when no code qualifies

If no error code occurs at least `k` times within the window, return an empty list.

Source: SRC-001.

- **AC-004-01:** The result is non-null and has size zero when every in-window count is below `k`.

### REQ-005 — Support the stated valid input domain

Support `1 <= n, k <= 100,000`, code lengths from 1 through 10, timestamps from 1 through `1,000,000,000`, `1 <= t <= timestamps[n - 1]`, alphanumeric error codes, equal non-empty list sizes, and timestamps sorted in non-decreasing order.

Source: SRC-001, SRC-002.

- **AC-005-01:** Correctly process the single-entry minimum.
- **AC-005-02:** Correctly process 100,000 entries, including 100,000 equal timestamps.
- **AC-005-03:** Design review establishes time and space bounds suitable for 100,000 entries.

### REQ-006 — Preserve the required Java operation and exercise compatibility

Expose `bmv.error_tracker.Result.getErrorCodes(int k, int t, List<Integer> timestamps, List<String> errorCodes)`, returning `List<String>`, under the existing source root using only Java 17-compatible language and library features.

Sources: SRC-004, SRC-005, SRC-006, SRC-007.

- **AC-006-01:** The declaration compiles through Maven without a dependency or build change.
- **AC-006-02:** The declaration independently compiles with `javac --release 17`.
- **AC-006-03:** The method remains public and static with the required name, parameter order/types, and return type.
- **AC-006-04:** Changes remain confined to this exercise and its handoff/test artifacts.

## Scope and exclusions

In scope are the inclusive recent-window calculation, per-code frequency, threshold inclusion, distinct results, lexicographic ordering, the empty result, stated scale, and the static Java operation.

Null lists or elements, unequal list sizes, empty lists, `k` or `t` outside the stated bounds, unsorted timestamps, out-of-range timestamps, and non-alphanumeric or overlength codes are outside the stated domain. Their return values, exception types, and validation order are unspecified. The sources specify no CLI/HTTP behavior, timestamp unit conversion, locale-specific collation, case folding, input mutation, concurrency guarantee, I/O, persistence, security boundary, ownership transfer, or resource lifecycle.

## Decisions and assumptions

- **DEC-001:** SRC-001 through SRC-004 define expected behavior; later tests must use literal source-derived results rather than the production algorithm as an oracle.
- **DEC-002:** Place the starter's `Result` class in package `bmv.error_tracker` to match repository layout; this is the only packaging adaptation.
- **DEC-003:** Interpret ascending lexicographic order as Java `String` natural order. This preserves exact alphanumeric values and performs no locale or case normalization.
- **DEC-004:** Preserve the shared Maven Java 25 setting because changing it would affect unrelated exercises. Exercise declarations and later implementation must additionally compile with `--release 17`.
- **DEC-005:** Invalid-input behavior remains unspecified and will not be tested.
- **ASM-001 (non-blocking):** Corresponding arrays in the prompt mean the two lists have the same positive size `n`.

No source conflict or unresolved question affects correctness, algorithm choice, the public API, threading, or compatibility.
