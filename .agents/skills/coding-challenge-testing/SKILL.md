---
name: coding-challenge-testing
description: Create independent black-box contract tests and mock bindings from a completed coding-challenge design handoff. Use when asked to run the testing phase before production implementation; finish by asking to run development.
---

# Coding Challenge Testing

Create reusable black-box tests from the solution-design documents. Validate
test mechanics with mock bindings, then hand off to development. Passing this
phase does not establish production correctness.

## Route to the configured agent

- If the current agent is not the custom agent named `coding-challenge-testing`,
  explicitly ask: “Should I run this skill as the configured
  `coding-challenge-testing` sub-agent, or continue in the current chat?” Do not
  begin substantive skill work until the user chooses.
- If the user chooses the sub-agent, delegate the complete request once to that
  agent. Include the user's request, target paths, relevant conversation context,
  and this skill path. Wait for the delegated agent to finish and relay its result,
  including its development-skill handoff question.
- If the user chooses the current chat, execute this skill directly without
  delegation for this invocation.
- If the current agent is `coding-challenge-testing`, execute this skill directly
  without asking the routing question again, and do not delegate the same workflow
  again.
- If the named agent is unavailable, report the configuration problem explicitly;
  do not silently run the workflow with a different model.

## Load the design handoff

- Read project-root `AGENTS.md`, applicable nested instructions, `README.md`,
  `pom.xml`, the raw challenge, relevant declarations, callers, and existing tests.
- Use the `ai/<task-name>/` directory provided by the user or the previous phase.
  Ask for the intended directory only when context does not identify it uniquely.
- Read `requirements.md`, `architecture.md`, `api-contract.md`, `traceability.md`,
  and `execution-log.md` there. Confirm the design is complete, the declared
  contract version matches its abstractions, and no blocking question remains.
- If required documents, declarations, or contract semantics are missing or
  conflicting, report the specific gap and ask for its resolution. Continue
  independent test planning; do not invent contracts or implement production
  behavior to bypass an incomplete design.

## Build independent contract suites

1. Search for suitable existing suites, fixtures, construction seams, and test
   dependencies. Extend compatible tests instead of duplicating them. Keep tests
   in `src/test` and preserve the configured JDK 25/Maven 3.9+ build.
2. Derive inputs and expected results from requirements and contracts, never from
   production algorithms or their observed outputs. Assign stable `TEST-001`
   style IDs and map them to acceptance criteria and interface/API operations.
3. Exercise the public contract: returned values, visible state transitions,
   specified errors, invariants, and contractual side effects. A required concrete
   class is a valid public boundary. Avoid private internals and interaction
   counts unless interactions are explicitly contractual.
4. Cover every acceptance criterion, applicable boundaries and failures. Leave
   unspecified invalid-input behavior unspecified. Add stateful, concurrency,
   mutation, or resource-lifecycle assertions only when the contract requires them.
   For concurrent contracts, use controlled interleavings, bounded waits, visible
   worker failures, and cleanup rather than sleep-only coordination.
5. Put reusable scenarios and assertions in one contract suite with the design's
   replaceable subject factory or fixture seam. Mock and production bindings must
   run the same inputs and assertions without duplicating the suite.
6. Supply deterministic mock subjects or minimal contract fixtures. Keep their
   programmed behavior separate from assertion expectations: do not feed expected
   values from test cases into the subject factory or echo assertions through a
   mock. Independently declared responses may share stable scenario IDs. This
   intentional test-data duplication does not validate production behavior.
7. For stateful contracts, use the smallest faithful fixture; do not build a second
   full production algorithm. Flag cases that fixtures cannot meaningfully validate
   and preserve their production acceptance scenarios for development.
8. Add applicable API scenarios through the public entry point with mocked internal
   collaborators. If the entry point is a temporary fixture, explicitly record that
   actual API integration remains unverified until real components are wired.

Own tests, fixtures, and testing sections of the handoff documents. Do not add the
business implementation or silently change declarations or contract semantics.
If a contract revision is needed, resolve the ambiguity and record the revised
design/version before changing dependent assertions. Follow project dependency
rules for any justified test dependency change; preserve Mockito startup settings.

## Validate and document

- Run the mock-backed suites explicitly with
  `mvn -Dtest=ActualMockSuiteClass test`, substituting actual class names. Confirm
  discovery and meaningful test totals, not just a successful process exit.
- Name or arrange temporary mock bindings so ordinary final acceptance does not
  count them as production tests. Document an explicit mock-stage command and the
  intended production binding names. With the current Surefire build, names such
  as `ExampleContractTest` are discovered; do not assume `*IT` runs automatically.
- Inspect the assertions against the specification separately from executing the
  mocks. A green programmed mock demonstrates compilation, discovery, fixtures,
  and test mechanics only. It cannot prove the expected answers are correct.
- Update `traceability.md` with actual test IDs/classes/methods and any planned
  non-test verification. Update `execution-log.md` with the contract version,
  changed files, exact commands, exit codes, totals, failures, errors, skips,
  report paths, coverage gaps, and fixture limitations.
- Record how development must instantiate production subjects and compose real
  in-scope API components, which external doubles may remain, and which temporary
  mock bindings must be excluded from production acceptance.

## Complete this phase and ask for development

Mark testing complete only when every acceptance criterion has applicable tests
or a documented non-test verification method, expectations have a contractual
basis, mock suites run successfully, and real bindings can replace mocks without
rewriting assertions. Mock-stage success must be labeled as such; production
behavior and actual integration remain unverified until development.

Finish with links to tests and updated handoff documents, coverage, mock-stage
results, and production binding instructions. Then explicitly ask:

“Shall I run `$coding-challenge-development` using `ai/<task-name>/`?”

Replace the task placeholder with the actual directory. This pause is the
user-requested phase handoff. Wait for their response before implementing. If
testing is blocked, report the blocker instead of offering a ready handoff.
