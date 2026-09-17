# Longest Common Prefix API contract

## Contract version 1

This task exposes a Java API with no CLI, HTTP, persistence, or external-system boundary.

### API-001 — Compute the longest common prefix

Traces to REQ-001, REQ-002, and REQ-003.

```java
LongestCommonPrefix operation = new LongestCommonPrefix();
String result = operation.longestCommonPrefix(strs);
```

The class is `bmv.prefix.LongestCommonPrefix`; the method is public, accepts `String[]`, and returns `String`.

### Input and output

Contract-version-1 input contains 1–200 non-null strings. Each has 0–200 characters and each nonempty value contains only lowercase English letters.

For valid input, the non-null result is the longest string that starts at index zero of every element. With no shared first character or any empty element it is `""`; with one element it is that element; if the shortest element prefixes every other element it is that shortest element. Matching stops at the first differing position.

For returned value `p`, every `strs[i].startsWith(p)` is true and no longer string is a prefix of every input.

Behavior is unspecified for a null or empty array, null elements, more than 200 elements, strings longer than 200 characters, uppercase letters, or other characters. Callers must not depend on observed behavior for those values.

Contract version 1 defines the returned value only. It adds no input-array mutation or concurrent-use guarantee. The planned implementation needs no mutable state, I/O, or external resource.

### Examples

| Input | Output |
| --- | --- |
| `["flower", "flow", "flight"]` | `"fl"` |
| `["dog", "racecar", "car"]` | `""` |
| `["prefix"]` | `"prefix"` |
| `["abc", "", "ab"]` | `""` |
| `["inter", "internet", "internal"]` | `"inter"` |
| `["abc", "abd", "ab"]` | `"ab"` |

### Compatibility

Version 1 preserves package `bmv.prefix`, public class `LongestCommonPrefix`, public no-argument construction, method name `longestCommonPrefix`, parameter type `String[]`, and return type `String`. The existing `MainBk` caller remains source-compatible.
