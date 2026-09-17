# Longest Common Prefix requirements

## Status

- Stage: 1 — complete
- Raw requirements folder: `/Users/mbugai/work/coding-challenges/src/main/bmv/prefix`
- Correctness-blocking questions: none
- Missing or unreadable source material: none

## Source inventory

| Source ID | Path and section | Role |
| --- | --- | --- |
| SRC-001 | `src/main/bmv/prefix/task-img.png`, problem statement | Authoritative functional behavior and empty-prefix result |
| SRC-002 | `src/main/bmv/prefix/task-img.png`, examples 1–2 | Authoritative examples |
| SRC-003 | `src/main/bmv/prefix/task-img.png`, constraints | Authoritative valid input domain |
| SRC-004 | `src/main/bmv/prefix/LongestCommonPrefix.java` | Existing public Java boundary; its comment marks the algorithm buggy and obsolete |
| SRC-005 | `src/main/bmv/MainBk.java:93` | Existing caller demonstrating construction and invocation |
| SRC-006 | `README.md` | Required JDK/Maven versions, source roots, test command, and report location |
| SRC-007 | `pom.xml` | Java 25, Maven source roots, JUnit/Mockito, and Surefire configuration |
| SRC-008 | `.aiassistant/rules/AGENTS.md` | Project-wide implementation and verification constraints |
| SRC-009 | `ai/multi-agent-execution-plan.md` | Six-stage workflow and traceability requirements |

The raw requirements folder contains exactly `LongestCommonPrefix.java` and `task-img.png`. The image references no additional material. Repository search found no prefix-specific tests.

## Goal and terminology

A Java caller supplies an array of strings and receives the longest string that begins at character index zero of every supplied string. A valid input satisfies REQ-002.

## Functional requirements

### REQ-001 — Compute the longest common prefix

For every valid input array, return the longest string that is a prefix of every element.

Sources: SRC-001, SRC-002.

- **AC-001-01:** Given `["flower", "flow", "flight"]`, return `"fl"`.
- **AC-001-02:** Given `["dog", "racecar", "car"]`, return `""`.
- **AC-001-03:** If no nonempty prefix is shared by every element, return `""`.
- **AC-001-04:** Given one valid string, return that entire string.
- **AC-001-05:** Given an input containing an empty string, return `""`.
- **AC-001-06:** If one string is the prefix of every other string, return that entire shortest prefix string.
- **AC-001-07:** Compare characters exactly and stop the common prefix at the first differing position.

### REQ-002 — Support the stated valid input domain

The operation must support an array length from 1 through 200 inclusive, each string length from 0 through 200 inclusive, and lowercase English letters only for nonempty strings.

Source: SRC-003.

- **AC-002-01:** Correctly process the minimum array size of one.
- **AC-002-02:** Correctly process an empty string element.
- **AC-002-03:** Correctly process 200 input strings.
- **AC-002-04:** Correctly process strings of length 200.
- **AC-002-05:** Correctly process 200 strings each of length 200.

Null arrays, null elements, empty arrays, arrays longer than 200, strings longer than 200, uppercase letters, and other characters are outside the source-defined contract. No validation behavior or exception type is an acceptance requirement.

### REQ-003 — Preserve the public Java operation

The exercise remains callable as:

```java
new bmv.prefix.LongestCommonPrefix()
    .longestCommonPrefix(String[] strs)
```

Sources: SRC-004, SRC-005, SRC-008.

- **AC-003-01:** Production code retains the package, public class, public no-argument construction, method name, parameter type, and `String` return type.
- **AC-003-02:** The existing caller remains source-compatible.

The current private helper is outside the public contract.

### REQ-004 — Keep contract assertions independent

The source comment explicitly says to ignore the existing algorithm as buggy and obsolete. Requirements, expected results, and contract assertions must derive from SRC-001 through SRC-003.

Source: SRC-004.

- **AC-004-01:** Independent review confirms tests neither invoke nor copy the obsolete algorithm to calculate expected values.

### REQ-005 — Keep the change scoped and build-compatible

Implementation and tests remain within the relevant exercise and existing source roots, preserve JDK 25 and Maven 3.9+ compatibility, and use the existing dependencies unless a demonstrated requirement necessitates a change.

Sources: SRC-006, SRC-007, SRC-008.

- **AC-005-01:** Relevant production and test code compiles with the configured Java 25 Maven build.
- **AC-005-02:** No unrelated exercise is changed.
- **AC-005-03:** No dependency or build-plugin change is introduced without separate justification under project rules.

## Scope

In scope: valid-domain behavior, the existing Java operation, independent black-box contract tests, and required build/suite verification.

Out of scope: CLI/HTTP/GUI layers, persistence, concurrency, networking, normalization, case folding, Unicode policy, locale-sensitive comparison, specified invalid-input outcomes, and the current private helper or algorithm as a required design.

## Nonfunctional observations

The source states no latency, asymptotic complexity, concurrency, security, lifecycle, or memory target. The maximum input contains 40,000 characters. Stage 2 will choose the simplest adequate approach and document its complexity. No dependency addition is required.

## Clarification and decision log

- **DEC-001:** SRC-001 through SRC-003 govern behavior; SRC-004 preserves only the public boundary because its algorithm is disclaimed.
- **DEC-002:** Invalid-input handling remains unspecified.
- **DEC-003:** No user clarification is required before Stage 2.
- **ASM-001:** Side effects are unspecified. Tests will not invent a no-mutation assertion, although a minimal implementation can naturally avoid mutation.
