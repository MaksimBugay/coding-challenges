# Removable Indices requirements

## Status

- Phase: solution design
- Contract version: 1
- Raw requirements folder: `src/main/bmv/removable_indices/`
- Correctness-blocking questions: none
- Missing or unreadable source material: none

## Source inventory

| Source ID | Path and section | Role |
| --- | --- | --- |
| SRC-001 | `src/main/bmv/removable_indices/task-img-full.png`, Problem Statement | Authoritative functional behavior, increasing index order, zero-based indexing, and impossible-case result |
| SRC-002 | `src/main/bmv/removable_indices/task-img-full.png`, Example | Authoritative repeated-character example |
| SRC-003 | `src/main/bmv/removable_indices/task-img-full.png`, Function Description and Output Format | Authoritative operation name, Java parameter/result types, and output semantics |
| SRC-004 | `src/main/bmv/removable_indices/task-img-full.png`, Constraints | Authoritative valid input domain |
| SRC-005 | `src/main/bmv/removable_indices/task-img-full.png`, Java starter | Authoritative `Result.getRemovableIndices` static boundary |
| SRC-006 | `README.md` and `pom.xml` | JDK 25, Maven 3.9+, source roots, dependencies, and build configuration |
| SRC-007 | `AGENTS.md` and `.aiassistant/rules/AGENTS.md` | Project-wide scope, design, reuse, and verification rules |
| SRC-008 | repository implementation, caller, test, and handoff search | No existing removable-indices implementation, caller, test, or task handoff exists |

The raw requirements folder contains exactly one readable 1375-by-756 PNG. It references no external documents. The screenshot also contains interview-chat text and a generated `Solution.main`; neither adds behavior beyond SRC-001 through SRC-005.

## Terminology

For a zero-based index `i` in `str1`, removing `i` means concatenating the characters before `i` with those after `i`, preserving their order. An index is removable when that result equals `str2` exactly.

## Functional requirements

### REQ-001 — Return every removable index

For valid `str1` and `str2`, return every zero-based index in `str1` whose single-character removal makes `str1` equal to `str2`.

Sources: SRC-001, SRC-002.

- **AC-001-01:** For `str1 = "abdgggda"` and `str2 = "abdggda"`, return `[3, 4, 5]` because removing any `g` at those positions produces `str2`.
- **AC-001-02:** A unique removable character produces a one-element result.
- **AC-001-03:** A repeated run may produce multiple removable indices, including a run at the beginning or end.
- **AC-001-04:** Removal may occur at index `0` or at `str1.length() - 1`.
- **AC-001-05:** Every returned index independently produces `str2` after exactly one removal.

### REQ-002 — Return indices in increasing order

When one or more removable indices exist, return them once each in strictly increasing numeric order.

Sources: SRC-001, SRC-003.

- **AC-002-01:** The example returns `[3, 4, 5]`, not another ordering.
- **AC-002-02:** The result contains no duplicate index.

### REQ-003 — Represent impossibility as `[-1]`

When no character can be removed from `str1` to produce `str2`, return a list whose sole element is `-1`.

Sources: SRC-001, SRC-003.

- **AC-003-01:** Inputs with incompatible characters and no valid removal return exactly `[-1]`.
- **AC-003-02:** `-1` never accompanies a valid nonnegative index.

### REQ-004 — Support the stated valid input domain

Support lowercase-English-letter strings where `2 <= str1.length() <= 200_000`, `1 <= str2.length() <= 200_000`, and `str1.length() == str2.length() + 1`.

Source: SRC-004.

- **AC-004-01:** Correctly process the minimum valid lengths 2 and 1.
- **AC-004-02:** Correctly process `str1` of length 200,000 and `str2` of length 199,999.
- **AC-004-03:** Correctly return as many as 200,000 indices when every character position is removable.
- **AC-004-04:** Design review establishes linear time and output-sensitive space suitable for the maximum input and result sizes.

### REQ-005 — Preserve the required Java operation and project layout

Expose the screenshot's static operation as `bmv.removable_indices.Result.getRemovableIndices(String str1, String str2)`, returning `List<Integer>`, under the existing source roots and JDK 25/Maven build.

Sources: SRC-003, SRC-005, SRC-006, SRC-007.

- **AC-005-01:** The production declaration and direct production test binding compile with Maven.
- **AC-005-02:** The method remains public and static with the stated name, parameter order/types, and return type.
- **AC-005-03:** No dependency or build change is introduced.
- **AC-005-04:** Changes remain confined to this independent exercise and its handoff/test artifacts.

## Scope and exclusions

In scope are valid-domain result values, complete enumeration, ordering, boundary positions, impossible results, the static Java operation, scale suitability, and independent black-box verification.

Inputs containing nulls, invalid lengths, a length difference other than one, empty values, uppercase characters, or non-English-lowercase characters are outside the stated domain. Their returned value, exception type, and validation order are unspecified. The sources also specify no CLI/HTTP contract, input identity guarantee, concurrency guarantee, security boundary, I/O, persistence, shared state, or resource lifecycle.

## Decisions and assumptions

- **DEC-001:** SRC-001 through SRC-005 are authoritative; expected test results will be derived directly from them rather than the later production algorithm.
- **DEC-002:** Follow the repository's package-per-exercise convention by placing the screenshot's `Result` type in `bmv.removable_indices`; this is the only packaging adaptation.
- **DEC-003:** Use exact Java character comparison. For the valid lowercase-English domain, UTF-16 representation introduces no ambiguity.
- **DEC-004:** Invalid-input behavior remains unspecified and will not be tested.
- **ASM-001 (non-blocking):** Because strings are immutable and the operation has no collaborator, no separate mutation or lifecycle contract is needed.

No source conflicts or unresolved questions affect correctness, algorithm choice, the public operation, threading, or compatibility.
