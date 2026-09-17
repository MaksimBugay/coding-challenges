---
name: coding-challenge-solution-design
description: Turn raw coding-challenge requirements into versioned contracts, minimal compilable abstractions, and Markdown handoffs for testing and development. Use when asked to design or prepare a challenge solution before tests and implementation.
---

# Coding Challenge Solution Design

Complete requirements analysis and contract design for the requested exercise.
This is the first phase of a three-skill workflow. Deliver the design and ask to
run testing; do not automatically start tests or business implementation.

## Route to the configured agent

- If the current agent is not the custom agent named
  `coding-challenge-solution-design`, explicitly ask: “Should I run this skill as
  the configured `coding-challenge-solution-design` sub-agent, or continue in the
  current chat?” Do not begin substantive skill work until the user chooses.
- If the user chooses the sub-agent, delegate the complete request once to that
  agent. Include the user's request, target paths, relevant conversation context,
  and this skill path. Wait for the delegated agent to finish and relay its result,
  including its testing-skill handoff question.
- If the user chooses the current chat, execute this skill directly without
  delegation for this invocation.
- If the current agent is `coding-challenge-solution-design`, execute this skill
  directly without asking the routing question again, and do not delegate the
  same workflow again.
- If the named agent is unavailable, report the configuration problem explicitly;
  do not silently run the workflow with a different model.

## Establish context

- Read the project-root `AGENTS.md` and applicable nested instructions, the
  challenge, relevant implementation, callers, tests, `README.md`, and `pom.xml`.
  Explicit user instructions and challenge constraints take precedence.
- Use the raw requirements path already supplied in the request or conversation.
  If it is missing or ambiguous, ask which folder contains the requirements;
  inspect independent project context while awaiting the answer.
- Inventory relevant files recursively, including images, nested documents, and
  referenced material. Record source paths and sections. Report missing or
  unreadable material and do not infer its contents.
- Reuse the task's existing `ai/<task-name>/` directory when identified. Otherwise
  derive a descriptive task name from the exercise. Preserve unrelated artifacts;
  ask if multiple existing tasks could be the intended target.

## Specify requirements

Extract functional behavior, inputs, outputs, constraints, edge cases, acceptance
criteria, and exclusions. Include performance, concurrency, security, ownership,
and lifecycle requirements only when supported by the sources. Distinguish
explicit requirements, proposed assumptions, and unresolved questions.

Assign stable IDs such as `REQ-001` and `AC-001-01`, with source references and
verifiable acceptance criteria. Consolidate duplicates without losing provenance.
Do not derive expected behavior from an implementation that the challenge marks
obsolete or incorrect.

Ask focused questions before committing to decisions affected by uncertainty in
correctness, algorithms, public APIs, threading, or compatibility. Explain the
alternatives and recommendation, continue independent work, and record answers.
Do not declare the design ready with correctness-blocking questions unresolved.

## Define the smallest testable design

1. Search for reusable behavior with `rg` or IDE search, then inspect candidate
   code, callers, and tests for matching contracts. Prefer existing project code,
   JDK facilities, then existing dependencies. Respect algorithm exercises.
2. Define cohesive boundaries tied to requirements. A function or required
   concrete class can be the complete abstraction; do not add an interface for
   every class, speculative strategies, or an unrequested API layer.
3. Specify signatures, construction, input domains, outputs, exceptions,
   preconditions, postconditions, invariants, and applicable side effects,
   mutation, ordering, ownership, lifecycle, and threading guarantees. Explicitly
   identify unspecified behavior rather than inventing validation requirements.
4. Describe the actual public boundary (Java API, CLI, HTTP, or other required
   form) with success, boundary, and contractual failure examples. Preserve
   required signatures, formats, independent submissions, JDK 25/Maven 3.9+,
   and existing source roots.
5. Add only the minimal compilable declarations needed by tests in `src/main`.
   Reuse suitable existing declarations without replacing working implementations
   with stubs. For a new concrete boundary that requires a method body, use an
   explicitly documented fail-fast placeholder; record its removal as development
   work. Implement no business behavior in this phase.
6. Define a small test construction seam so the same inputs and assertions can
   run against mock and production bindings. Preserve a required concrete public
   API; do not force an interface just to mock it. Preserve the existing Mockito
   agent configuration and follow project dependency rules for any necessary
   build change.
7. Create work packages with requirement IDs, contract version, permitted files,
   dependencies, reuse choices, expected time/space complexity, and completion
   criteria. Assign ownership by phase; no concurrent edits to shared files.

## Write the handoff documents

Create or update these files in `ai/<task-name>/`; keep each fact in its owning
document and link to it from the others instead of copying entire contracts.

| File | Required contents |
| --- | --- |
| `requirements.md` | Source inventory, stable requirements and acceptance IDs, scope, exclusions, clarifications, decisions, assumptions |
| `architecture.md` | Reuse findings, requirement-linked boundaries (`IF-001`), signatures, construction seams, versioned contracts, work packages and completion criteria |
| `api-contract.md` | Versioned public operations (`API-001`), input/output/error semantics and concrete examples |
| `traceability.md` | Requirement → acceptance criterion → interface/API → planned test or non-test verification → implementation work package |
| `execution-log.md` | Phase status, current contract version, ownership, changed files, decisions, actual commands/results, blockers, and next phase |

The tester must be able to derive expected results from these documents without
consulting the production algorithm. The coder must be able to implement the
contracts without changing the tester's assertions. Record concrete declaration
paths, intended test paths, mock/production binding instructions, and applicable
test commands in `architecture.md`.

Check declaration compilation with the configured build (normally
`mvn -DskipTests compile`). Report the actual result, including environment or
pre-existing failures. Review the design for duplication, unnecessary abstractions,
missing source material, untestable contracts, and incomplete traceability.

## Complete this phase and ask for testing

Mark design complete only when sources and scope are accounted for, blocking
questions are resolved, required declarations compile, and every requirement has
an implementation boundary and planned verification. Record an exact blocker if
completion is prevented; do not present a blocked phase as ready for testing.

Finish with links to the handoff documents, declarations changed, contract version,
validation results, and any non-blocking assumptions. Then explicitly ask:

“Shall I run `$coding-challenge-testing` using `ai/<task-name>/`?”

Replace the task placeholder with the actual directory. This pause is the
user-requested phase handoff. Wait for their response before running testing.
