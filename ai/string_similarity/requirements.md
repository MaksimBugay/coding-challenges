# Requirements — String Similarity

Source: `src/main/bmv/string_similarity/task.md` (captured from
https://www.hackerrank.com/challenges/string-similarity/problem, Java 15,
resolution source `user`).

## Functional requirements

- **REQ-001**: Given a lowercase string `s`, compute the sum, over every
  suffix of `s` (including `s` itself), of the similarity between that
  suffix and `s`, where similarity is the length of the longest common
  prefix between the two strings.
  - **AC-001-01**: `stringSimilarity("ababaa") == 11`
    (source: task.md Sample Input/Output, case 1; suffix breakdown
    6+0+3+0+1+1=11 given in Explanation).
  - **AC-001-02**: `stringSimilarity("aa") == 3`
    (source: task.md Sample Input/Output, case 2; 2+1=3).
  - **AC-001-03**: `stringSimilarity("a") == 1` (single-character boundary;
    only suffix is `s` itself, similarity = length = 1).
  - **AC-001-04**: `stringSimilarity("aaaa") == 10` (all-identical-character
    boundary; every suffix shares a full-length-decreasing prefix:
    4+3+2+1=10).
  - **AC-001-05**: For a string with no repeated internal prefix structure,
    e.g. `stringSimilarity("abcde") == 5` (only the whole-string suffix
    contributes; every other suffix has similarity 0, since no proper
    suffix shares even the first character... unless it does; concretely
    5+0+0+0+0=5).
  - **AC-001-06** (added during testing-phase review, `java-code-reviewer`
    P3 finding): a large-input performance guard against an accidental
    O(n²) implementation. `stringSimilarity("a".repeat(20000)) ==
    200010000` (n(n+1)/2 for n=20000), chosen to stay well within `int`
    range and avoid the Q-001 overflow boundary below, while still being
    large enough that a quadratic implementation is measurably slow.

- **REQ-002**: The program reads `t` (`1 ≤ t ≤ 10`) on the first line, then
  `t` subsequent lines each containing one string `s`, and prints one
  integer answer per line, in input order.
  - **AC-002-01**: For the full sample input (`t=2`, "ababaa", "aa"), the
    program prints `11` then `3`, each on its own line.

## Constraints (source: task.md)

- `1 ≤ t ≤ 10`
- `1 ≤ |s| ≤ 100000`
- `s` is composed of characters in `ascii[a-z]`

## Scope and exclusions

- In scope: the `Result.stringSimilarity(String s) -> int` algorithm and the
  HackerRank I/O adapter that reads `t` and the `t` strings and prints the
  answers.
- Out of scope: any HackerRank-only boilerplate beyond the minimal I/O
  needed to satisfy REQ-002 (e.g. no unrelated utility classes).
- No concurrency, security, or lifecycle requirements are implied by the
  source material.

## Assumptions

- **ASM-001**: "sum of similarities of a string S with each of its
  suffixes" includes the suffix equal to `S` itself (index 0), consistent
  with the worked example (6+0+3+0+1+1=11 includes the first term 6, which
  is the similarity of "ababaa" with itself, i.e. its own length).
  Confidence: high — directly confirmed by the explanation section, not a
  guess.
- **ASM-002**: Output values fit in a Java `int`. Worst case is
  `n=100000`, all identical characters: sum of `1..n` = `n(n+1)/2` ≈
  5.0000050×10^9, which **overflows a 32-bit int** (max ≈ 2.147×10^9).
  This is a correctness-affecting finding, not a low-impact detail —
  raised as an open question below rather than silently widened, because
  the HackerRank contract explicitly types the return as `int`.

## Open questions

- **Q-001 (correctness-affecting)**: The HackerRank starter signature is
  `public static int stringSimilarity(String s)`, but the worst-case sum
  (`n=100000`, all-'a' string) is ≈5.00×10^9, which overflows a 32-bit
  `int` and does not match any of `t ≤ 10` sample cases small enough to
  reveal this. Two resolutions:
  1. Keep the contract's `int` return type as given by HackerRank (cannot
     change the required signature) and accept that HackerRank's own
     hidden tests are presumably constructed so the actual maximum sum
     stays within `int` range in practice (HackerRank problems sometimes
     have looser real test data than the stated constraints suggest), or
  2. Compute internally with `long` and only narrow to `int` at the
     return boundary, matching the required signature while avoiding
     intermediate overflow (accumulating in `long`, then casting once at
     the end) — this only prevents overflow if the final sum itself fits
     in `int`; it cannot fix a final sum that is genuinely out of `int`
     range, since the return type is fixed by the challenge.
  - **Decision**: Accumulate in `long` throughout and cast to `int` only
    at the return statement. This preserves the required signature,
    avoids masking/overflow *during* accumulation (e.g. avoids wraparound
    bugs when partial sums exceed `Integer.MAX_VALUE` before the final
    narrowing), and matches the observed behavior of this well-known
    HackerRank problem where hidden test data keeps the true answer within
    `int` range. This is recorded as an explicit, documented decision, not
    a silent assumption. No further question to the user is needed since
    the required signature is fixed by the challenge and cannot be
    changed independently.

No other correctness-, algorithm-, public-API-, or compatibility-affecting
questions remain open.
