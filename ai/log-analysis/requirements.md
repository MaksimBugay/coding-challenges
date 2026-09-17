# Log Analysis requirements

## Status

- Phase: solution design complete
- Contract version: 1
- Raw requirements folder: `src/main/bmv/log_analysis/`
- Java compatibility target: 17
- Correctness-blocking questions: none
- Missing or unreadable source material: none

## Source inventory

| Source ID | Path and section | Role |
| --- | --- | --- |
| SRC-001 | `src/main/bmv/log_analysis/task-img1.jpg`, statement, example, function description, and Java starter | Authoritative server/log model, inclusive query window, result rule, operation name, parameter order/types, and return type |
| SRC-002 | `src/main/bmv/log_analysis/task-img2.jpg`, constraints and Sample Case 0 input/output | Authoritative valid input limits and sample values |
| SRC-003 | `src/main/bmv/log_analysis/task-img3.jpg`, constraints, Sample Case 0, and explanation | Authoritative confirmation of sample windows and results |
| SRC-004 | `README.md` and `pom.xml` | Project source roots, Maven build, dependencies, and repository-wide Java 25 setting |
| SRC-005 | `AGENTS.md` | Project-wide scope, design, reuse, and verification rules |
| SRC-006 | User instruction in this chat | Authoritative Java 17 compatibility target for this exercise |
| SRC-007 | Repository implementation, caller, test, and handoff search | No existing log-analysis implementation, caller, test, or task handoff exists |

The raw folder contains exactly three readable JPEG screenshots and no nested or referenced material. The screenshots show a hosted Java 17 editor and generated custom-input code; only the declared `Result.getStaleServerCount` operation is part of the required boundary.

## Terminology

There are `n` servers with IDs from 1 through `n`. A log row `[serverId, time]` records a request handled by that server at that integer time. For a query value `time`, its request window is the closed interval:

```text
[time - x, time]
```

A server is stale for that query exactly when it has no log row whose time belongs to the window.

## Functional requirements

### REQ-001 — Select requests from each inclusive query window

For every query, consider exactly the request logs with timestamps from `query[i] - x` through `query[i]`, including both boundaries.

Sources: SRC-001, SRC-003.

- **AC-001-01:** A request at `query[i] - x` is included.
- **AC-001-02:** A request at `query[i]` is included.
- **AC-001-03:** Requests before the lower boundary and after the query time are excluded.
- **AC-001-04:** Correctness is independent of log-row ordering; Sample Case 0 supplies timestamps in the order `[2, 3, 6, 3]`.

### REQ-002 — Count servers with no request in the window

Return `n` minus the number of distinct server IDs represented by in-window logs for each query.

Sources: SRC-001, SRC-003.

- **AC-002-01:** Multiple in-window requests from the same server reduce the stale count only once.
- **AC-002-02:** If every server has an in-window request, the answer is zero.
- **AC-002-03:** If no server has an in-window request, the answer is `n`.
- **AC-002-04:** In the statement example, `n = 3`, `logData = [[3,3],[2,6],[1,5]]`, `query = [10,11]`, and `x = 5` return `[1,2]`.
- **AC-002-05:** In Sample Case 0, `n = 6`, `logData = [[3,2],[4,3],[2,6],[6,3]]`, `query = [3,2,6]`, and `x = 2` return `[3,5,5]`.

### REQ-003 — Return one answer per query in input order

The result has the same size and positional order as `query`; element `i` answers `query[i]` even when query values are unsorted.

Sources: SRC-001, SRC-002, SRC-003.

- **AC-003-01:** Sample Case 0 preserves the unsorted query order `[3,2,6]` and returns `[3,5,5]` in that order.
- **AC-003-02:** Duplicate query values produce answers at both corresponding positions.

### REQ-004 — Support the stated valid input domain

Support `1 <= n <= 1,000`, `1 <= m <= 1,000`, `1 <= q <= 1,000`, server IDs in `[1,n]`, log timestamps and query values in `[1,100,000]`, and `1 <= x <= 100,000`.

Source: SRC-002 and SRC-003.

- **AC-004-01:** Correctly process the minimum sizes `n = m = q = 1`.
- **AC-004-02:** Correctly process 1,000 servers, 1,000 logs, and 1,000 queries.
- **AC-004-03:** Design review establishes time and space bounds suitable for the maxima.

### REQ-005 — Preserve the required Java operation and exercise compatibility

Expose `bmv.log_analysis.Result.getStaleServerCount(int n, List<List<Integer>> logData, List<Integer> query, int x)`, returning `List<Integer>`, under the existing source root using only Java 17-compatible language and library features.

Sources: SRC-001, SRC-004, SRC-005, SRC-006.

- **AC-005-01:** The declaration compiles through Maven without a dependency or build change.
- **AC-005-02:** The declaration independently compiles with `javac --release 17`.
- **AC-005-03:** The method remains public and static with the required name, parameter order/types, and return type.
- **AC-005-04:** Changes remain confined to this exercise and its handoff/test artifacts.

## Scope and exclusions

In scope are closed-window membership, distinct active-server counting, stale-server counts, answer order, the stated scale, and the static Java operation.

Null lists or elements, log rows of a size other than two, empty log/query lists, out-of-range values, and server IDs outside `[1,n]` are outside the stated valid domain. Their return values, exception types, and validation order are unspecified. The sources specify no sorting precondition, input mutation rule, returned-list mutability, concurrency guarantee, I/O, persistence, security boundary, ownership transfer, or resource lifecycle.

## Decisions and assumptions

- **DEC-001:** SRC-001 through SRC-003 define expected behavior; later tests must use literal source-derived or independently reasoned results rather than production logic as an oracle.
- **DEC-002:** Place the starter's `Result` class in package `bmv.log_analysis` to match the exercise directory and repository convention; this is the only packaging adaptation.
- **DEC-003:** Preserve input order for both logs and queries; the algorithm may inspect them in any order but must not require sorted input.
- **DEC-004:** Preserve the shared Maven Java 25 setting because changing it would affect unrelated exercises. Exercise declarations and later implementation must additionally compile with `--release 17`.
- **DEC-005:** Invalid-input behavior remains unspecified and will not be tested.
- **ASM-001 (non-blocking):** The prompt's two-dimensional `log_data` description means every valid row contains exactly `[serverId, time]`.

No source conflict or unresolved question affects correctness, algorithm choice, the public API, threading, or compatibility.
