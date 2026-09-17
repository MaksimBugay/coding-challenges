---
name: coding-challenge-development
description: Implement a coding challenge from its versioned design and black-box tests, wire production bindings, pass acceptance, then review solution classes and repair findings. Use when asked to run development after the solution-design and testing handoffs.
---

# Coding Challenge Development

Implement the agreed contracts, replace mock subjects with production bindings,
and complete verification and repair. Continue through fixable failures until
the final implementation satisfies the required acceptance checks.

## Route to the configured agent

- If the current agent is not the custom agent named
  `coding-challenge-development`, explicitly ask: “Should I run this skill as the
  configured `coding-challenge-development` sub-agent, or continue in the current
  chat?” Do not begin substantive skill work until the user chooses.
- If the user chooses the sub-agent, delegate the complete request once to that
  agent. Include the user's request, target paths, relevant conversation context,
  and this skill path. Wait for the delegated agent to finish and relay its result.
- If the user chooses the current chat, execute this skill directly without
  delegation for this invocation.
- If the current agent is `coding-challenge-development`, execute this skill
  directly without asking the routing question again, and do not delegate the
  same workflow again.
- If the named agent is unavailable, report the configuration problem explicitly;
  do not silently run the workflow with a different model.

## Establish the baseline

- Read project-root `AGENTS.md`, applicable nested instructions, the challenge,
  implementation, callers, tests, `README.md`, and `pom.xml` before editing.
- Identify the task's `ai/<task-name>/` handoff from the request or conversation;
  ask only if the target is missing or ambiguous. Read `requirements.md`,
  `architecture.md`, `api-contract.md`, `traceability.md`, and `execution-log.md`.
- Verify that design and mock-stage testing are complete, contract versions agree,
  and work packages and production binding instructions are available. Treat
  mock-stage results solely as test-setup evidence.
- If prerequisites or contracts are missing or inconsistent, identify the exact
  gap and request resolution. Continue independent investigation; do not invent
  expected behavior or bypass the preceding phase's acceptance criteria.
- Inspect scoped local changes and preserve unrelated user work. Use the work
  package file boundaries and dependency order; keep shared contracts, composition,
  and build files under one owner.

## Implement and integrate

1. Search for equivalent behavior and inspect its semantics, callers, and tests.
   Reuse suitable project code, then JDK facilities, then existing dependencies.
   Preserve independent challenge submissions and required algorithm exercises.
2. Implement every planned boundary and required API entry point, adapters, and
   composition wiring. Preserve signatures, input/output formats, exceptions,
   invariants, and any ownership, mutation, lifecycle, and concurrency guarantees.
   Remove all design-phase fail-fast placeholders from accepted behavior.
3. Apply SOLID with minimal design. Choose the simplest algorithm that satisfies
   the limits and record time and auxiliary-space complexity with defined
   variables. Introduce no speculative abstractions or unrelated cleanup.
4. Search for duplication before and after edits. Keep a shared rule in its
   owning implementation; explain intentional separation. Run an existing
   duplication checker when applicable without weakening thresholds.
5. Preserve JDK 25, Maven 3.9+, source roots, and Mockito startup configuration.
   If dependencies/build plugins must change, apply the complete project rules
   for necessity, current official release and Maven Central checks, exact pins,
   compatibility, advisories, and resolved dependency inspection. Report gaps.
6. Compile and run relevant focused checks as packages are integrated. Do not
   change contract assertions to accommodate the implementation.

## Accept real production behavior

- Bind the unchanged contract suites to actual production implementations through
  the prepared construction seam. Confirm they instantiate production subjects,
  rather than mocks or fixtures that replay expected results.
- Wire the real public entry point and all generated in-scope components into API
  integration tests. Unit tests may isolate collaborators at declared boundaries;
  acceptance must exercise the behavior being delivered.
- Keep doubles for out-of-scope external systems only where appropriate and
  document that remaining integration limit. Remove or clearly separate temporary
  mock-stage bindings from acceptance runs and counts.
- Run `mvn -Dtest=ActualTestClass test` with the real production suite names.
  Ensure Surefire discovers API suites; do not assume unconfigured `*IT` tests run.
- Run `mvn test` for final integrated acceptance, even when only one exercise was
  implemented, plus other relevant configured checks and non-test verification.
  The full suite includes `SlowProcessCollectorTest`, which creates 20 million
  records. If resources prevent completion, report exactly what ran and what did
  not; an incomplete run does not satisfy the all-tests-green criterion.

## Diagnose, repair, and revalidate

Record each failure in `execution-log.md`: requirement ID, failing test and input,
expected versus actual behavior, root cause, affected files/owner, and fix.
Distinguish product defects, incorrect tests, wiring defects, environment problems,
and evidenced pre-existing failures.

Fix the cause with the smallest compliant change and add a focused regression
case for newly discovered behavior defects. Correct a test only when the
specification supports the correction; review its expectation independently of
the implementation and record the evidence and reason. Never weaken assertions,
disable cases, or add exclusions solely to obtain a green run.

If a failure exposes a contract ambiguity, ask the focused question and continue
independent work. Resolve it and update the requirements, versioned contracts,
tests, and traceability before implementing the changed behavior. Do not silently
alter the frozen contract.

Re-run failing tests and relevant neighboring suites after repairs. Once focused
tests pass, run the complete required suite against the final state. Subsequent
changes require appropriate revalidation. Investigate flakiness rather than
retrying until a single run passes. Avoid repeated checks after a valid final run
unless new evidence or changes warrant them.

## Run the final solution review

After implementation, production binding, failure repair, and the required final
acceptance checks are otherwise complete, invoke `$java-code-reviewer` as the last
technical gate. The review target is every production Java solution class added
or changed for this exercise. Tests, fixtures, declarations, handoff documents,
callers, and validation results are review context, but test classes and fixtures
are not review targets.

The development invocation already authorizes this required nested review. Use
the execution context selected for development without asking another routing
question: a configured development agent delegates to the configured
`java-code-reviewer` agent, while development running in the current chat runs
the review in the current chat. Keep the reviewer read-only; this development
phase owns all resulting edits.

Treat every confirmed actionable finding, including `P3 Minor/Cosmetic`, as
required work:

1. Fix the root cause in the production solution with the smallest change that
   preserves the frozen requirements and contracts. Add or adjust a focused
   regression test when needed to demonstrate the defect and remedy.
2. Re-run the affected focused and neighboring tests after each repair set, then
   run the complete required suite because the production state changed. Update
   `traceability.md` and `execution-log.md` with the finding, disposition, changed
   files, exact commands, and current results.
3. Invoke `$java-code-reviewer` again on the repaired production solution classes.
   Repeat review, repair, and validation until it reports no actionable findings.

If a finding cannot be fixed within the agreed exercise scope, record the exact
blocker and leave development incomplete. Do not expand the review target to test
quality or unrelated repository code during this gate.

## Finish with evidence

Update `traceability.md` so each requirement links to its actual implementation
and passing test or completed non-test verification. Update `execution-log.md`
with exact commands, exit codes, totals, failures, errors, skips, report paths,
contract revisions, fixes, and final status. Use current results, not stale reports.

Review the final diff for contract fidelity, accidental changes, unnecessary
abstractions, duplication, and incomplete coverage. Declare completion only when
all required checks pass on the final production state, no acceptance placeholders
remain, the final solution review reports no actionable findings, and there are
no unresolved failures or required verification gaps.

If an external blocker prevents completion, record the blocker and required action
and mark the work incomplete; exhausting retries or passing a subset is not success.
Pre-existing failures still prevent an all-tests-green claim, but do not authorize
unrelated repairs. Request direction if solving them expands the exercise scope.

Finish with delivered behavior, changed files, reused code, complexity, applicable
threading analysis, exact validation and final reviewer results, repairs made, and
remaining documented assumptions. Stop when the requirement is satisfied; no
further skill handoff is required.
