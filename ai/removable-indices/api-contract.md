# Removable Indices API contract

## Contract version 1

This exercise exposes one synchronous Java operation. Requirements and valid-input limits are defined in [requirements.md](requirements.md).

## API-001 — Find every removable index

```java
List<Integer> result =
    bmv.removable_indices.Result.getRemovableIndices(str1, str2);
```

The method is `public static`, accepts `(String str1, String str2)` in that order, and returns `List<Integer>`.

### Input semantics

- `str1` and `str2` are non-null strings containing only lowercase English letters.
- `str1` has 2–200,000 characters.
- `str2` has 1–200,000 characters.
- `str1.length() == str2.length() + 1`.
- Indexing is zero-based.

### Output semantics

For each valid index `i`, remove exactly `str1.charAt(i)` and keep every other character in order. The returned list contains exactly those indexes for which the resulting string equals `str2`. Each appears once, and indexes are in strictly increasing order.

If at least one valid index exists, every result element is between `0` and `str1.length() - 1`. If none exists, the result is exactly `[-1]`. The list is always non-null for valid input.

### Concrete examples

| Description | `str1` | `str2` | Result |
| --- | --- | --- | --- |
| Required repeated-run example | `"abdgggda"` | `"abdggda"` | `[3, 4, 5]` |
| Unique middle removal | `"abc"` | `"ac"` | `[1]` |
| Repeated run at beginning | `"aabc"` | `"abc"` | `[0, 1]` |
| Repeated run at end | `"abcc"` | `"abc"` | `[2, 3]` |
| Remove final character | `"abcd"` | `"abc"` | `[3]` |
| Minimum lengths | `"aa"` | `"a"` | `[0, 1]` |
| Impossible | `"abc"` | `"de"` | `[-1]` |

### Boundary and failure semantics

A valid pair with no removable index is a normal successful invocation returning `[-1]`; it does not throw. Inputs outside the stated domain, including nulls, empty values, invalid length relationships, or disallowed characters, have no promised return value or exception. Callers must not depend on observed invalid-input behavior.

The operation has no declared I/O, persistence, external resource, collaborator, or shared-state lifecycle. Contract version 1 does not specify concurrent invocation behavior.

### Compatibility baseline

Version 1 freezes package `bmv.removable_indices`, public class `Result`, method name `getRemovableIndices`, `public static` access, parameter order/types `(String, String)`, and return type `List<Integer>`. The package is the repository-layout adaptation of the screenshot's otherwise unchanged Java starter boundary.
