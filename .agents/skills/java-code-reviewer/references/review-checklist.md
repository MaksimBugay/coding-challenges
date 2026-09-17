# Java 25 Review Checklist

Use this checklist for every review. Apply only checks relevant to the requested
scope, but always assess performance and thread safety.

## Scope and evidence

- For a diff review, inspect enough surrounding code, callers, and tests to
  establish impact. Separate defects introduced by the diff from pre-existing
  issues. For a whole-exercise review, assess the complete implementation.
- Establish inputs, outputs, constraints, edge cases, ownership, mutation and
  null policies, ordering, overflow behavior, and concurrency guarantees from
  the prompt, tests, and callers. Do not invent requirements.
- Ask a focused question when unresolved uncertainty can change correctness,
  the algorithm, public API, threading guarantees, dependency compatibility, or
  the review conclusion. Continue independent investigation where possible.
- Trace each suspected issue through an actual call path. Support it with a
  concrete input, legal thread interleaving, violated contract, complexity
  argument, test, or runtime observation.
- Separate confirmed defects from unresolved questions and measurement
  hypotheses. Do not report speculation as a finding or style preferences as
  defects.
- Preserve challenge signatures, input/output formats, independent submissions,
  configured source roots, and the required Java and Maven toolchain.

## Correctness and Java 25

- Check the algorithm against stated constraints and examples. Include relevant
  empty and singleton inputs, duplicates, boundaries, malformed input, integer
  overflow, and termination.
- Check equality and hashing, comparator contracts, mutable collection keys,
  iteration-order assumptions, generic type safety, exception behavior, and
  resource ownership where relevant.
- Verify failure paths leave valid state, close owned resources, shut down owned
  executors, and propagate asynchronous failures.
- Check Unicode, charset, locale, timezone, and floating-point assumptions only
  when the contract makes them relevant.
- Verify compiler release, runtime, test runner, plugins, and libraries are
  compatible with Java 25. Prefer stable Java 25 features that simplify the
  actual solution; do not require modernization for style alone.
- `StructuredTaskScope` is preview in Java 25. Flag unapproved preview or
  incubator use and missing flags. `ScopedValue` is final in Java 25, but suggest
  it only for a demonstrated context-sharing need.
- Consult official Java 25 documentation and current primary sources when an API
  guarantee, feature status, performance claim, dependency version, or security
  fact is uncertain. Report unavailable verification rather than guessing.

## Performance

- State time and auxiliary-space complexity for the main operations, defining
  variables and distinguishing worst-case from amortized behavior where needed.
- Check repeated scans, sorting, nested work, copying, expensive loop bodies,
  unsuitable collections, recursion depth, boxing, intermediate objects, and
  retained memory against the allowed input sizes.
- Where applicable, assess throughput, tail latency, contention, lock scope,
  blocking I/O, executor or connection capacity, bounded queues, backpressure,
  timeouts, retry amplification, cache bounds, TTL cleanup, and eviction.
- Relate each optimization to a required bound or evidence. Distinguish static
  complexity analysis from measured performance.
- For uncertain hotspots, propose a specific measurement. Prefer JFR for CPU,
  allocation, GC, and contention evidence, and JMH for justified JVM
  microbenchmarks with representative inputs, warmup, forks, result consumption,
  hardware/JDK details, and comparable baselines.

## Thread safety

- Classify the component as stateless or immutable, thread-confined, externally
  synchronized, internally thread-safe, or unsafe for intended concurrent use.
  If the exercise is sequential, explain why concurrency is not applicable.
- Inventory shared mutable state, aliases, static fields, callbacks, and mutable
  objects exposed through constructors or accessors. Do not equate a `final`
  reference, record, or unmodifiable wrapper with deep immutability.
- Check safe publication, happens-before relationships, constructor escape,
  lazy initialization, stale reads, visibility, atomicity, and multi-field
  invariants.
- Check compound actions and their atomic boundary, including check-then-act,
  read-modify-write, capacity checks, eviction, and TTL refresh or removal. A
  concurrent collection does not make a compound operation atomic.
- Check lock ordering, balanced release in `finally`, condition loops, deadlock,
  starvation, livelock, and calls to blocking or unknown code while holding locks.
- Check admission bounds, rejection, shutdown, deadlines, cancellation,
  interruption, cleanup, executor choice, common-pool blocking, nested waits,
  and lost asynchronous exceptions.
- Virtual threads suit blocking work but do not accelerate CPU work or remove
  synchronization needs. Bound access to scarce resources rather than pooling
  virtual threads. In Java 25, ordinary `synchronized` blocks do not pin virtual
  threads; require evidence before reporting pinning or demanding another lock.
- For every race finding, show a legal sequence of operations and the invariant
  it breaks. Prefer controlled concurrency tests; passing stress tests alone do
  not prove thread safety.

## Design, reuse, and dependencies

- Assess all five SOLID principles proportionately. Accept a pure function or
  small concrete class as a complete design. Do not demand speculative
  interfaces, strategies, dependency injection, or architectural layers.
- Search for equivalent behavior before recommending new code or dependencies.
  Prefer suitable project code, then the JDK, then existing dependencies, while
  respecting exercises that require a candidate-written algorithm.
- Check repeated business rules, validation, algorithms, and configuration.
  Share code only where callers have the same contract; preserve intentional
  alternative algorithms and independent exercises.
- Verify dependencies are necessary, declared and scoped correctly, exactly
  versioned or BOM-managed, and compatible with Java 25. Flag bundled JARs and
  unapproved ranges, snapshots, or prereleases.
- For dependency changes, verify the latest compatible stable release through
  official release information and Maven Central, inspect the resolved tree,
  and check current publisher advisories plus OSV or NVD. State the check date,
  applicability evidence, fixed version, and any unavailable verification.
- Inspect real trust boundaries for injection, traversal, unsafe deserialization,
  weak randomness, secret exposure, and unbounded resource consumption. Avoid a
  generic security checklist when the code has no relevant boundary.

## Validation and output

- Check that tests cover the contract, boundaries, failures, and regressions.
  For concurrency, assess controlled interleavings, visible worker failures,
  bounded waits, cleanup, and invariants; sleep-only coordination is weak evidence.
- Run focused existing tests with the actual test class names. Run the full suite
  for shared code or build/dependency reviews when feasible. Account for any
  documented resource-heavy tests and state every exclusion or limitation.
- Report only commands that actually ran, their results, and material coverage
  gaps. Never claim an unrun test, scan, benchmark, or duplication check passed.

For every finding, include:

1. **Severity and title** using the exact label defined in `SKILL.md`.
2. **Location** as a clickable file path and the smallest useful line range or
   symbol.
3. **Evidence and impact** describing the trigger or interleaving, actual versus
   required behavior, affected callers or users, and any demonstrated complexity
   or measured performance impact.
4. **Minimal remedy** naming reusable code where relevant, plus the focused test
   or measurement that would validate it.

Then report concise coverage using `checked`, `issue found`, `not applicable`
with a reason, or `unverified` for each of these areas:

- requirements and correctness;
- Java 25 and build compatibility;
- all five SOLID principles and minimality;
- reuse and duplication;
- dependency freshness and security;
- performance;
- thread safety; and
- tests.

Always state the main operations' performance assessment and the component's
threading contract, even when neither produces a finding. Include unresolved
questions that materially limit the conclusion.

Conclude with exactly one of `changes required`, `no actionable findings within
reviewed scope`, or `review incomplete`, followed by a brief reason. Keep optional
improvements separate from required fixes, but give every optional issue the
appropriate severity and maintain the same descending order.
