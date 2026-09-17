# AI multi-agent execution plan

## Objective and execution contract

Turn raw user requirements into a minimal implementation verified against explicit
interface and API contracts. Execute the six stages below in order, parallelizing
only independent work within a stage. This document is a reusable execution plan;
creating it does not start implementation.

All agents must read and follow the authoritative
[project rules](../.aiassistant/rules/AGENTS.md) at
`/Users/mbugai/work/coding-challenges/.aiassistant/rules/AGENTS.md`.
Explicit user instructions and challenge requirements take precedence as specified
there. Read relevant project instructions, the challenge, existing implementation,
callers, tests, `README.md`, and `pom.xml` before editing the affected code.

Preserve JDK 25, Maven 3.9+, the `src/main` and `src/test` source roots, required
signatures, input/output formats, and independent challenge submissions. Search
for reusable behavior before introducing code, abstractions, or dependencies.
Keep scope limited to the requested exercise.

## Agents and ownership

| Role | Responsibility | Owned outputs |
| --- | --- | --- |
| Coordinator | Ask user questions, manage dependencies and assignments, integrate work, enforce stage completion criteria | Execution status, decisions, traceability, final report |
| Requirements analyst | Inspect source material, resolve inconsistencies, specify acceptance criteria | Requirements specification, source inventory, clarification log |
| Contract architect | Define minimal boundaries, interface behavior, and public API | Architecture, interface contracts, API contract, work packages |
| Test agent | Derive independent black-box tests from requirements and contracts | Contract suites, fixtures, mock bindings, test coverage map |
| Implementation agents | Implement assigned interfaces and API components under project rules | Production code, reuse findings, implementation notes |
| Verification agent | Review contract fidelity and rule compliance, run integrated checks, classify failures | Validation evidence, defect log, completion assessment |

These are roles, not a requirement to launch all agents simultaneously. Respect
available concurrency limits. Keep test expectations independently owned from
implementation where possible; reuse agents sequentially if capacity is limited.

The coordinator assigns each work package an owner, requirement IDs, contract
version, dependencies, permitted files, and completion criteria. Agents must not
edit the same files concurrently. Shared contracts, composition code, and build
files have one designated owner. Integrate dependent packages in dependency order.

Every handoff includes changed files, satisfied requirement IDs, decisions,
commands actually run and their results, and unresolved blockers. Agents surface
contract changes to the coordinator instead of changing them independently.

## Working artifacts and traceability

At execution time, create `ai/<task-name>/` for task documentation. Keep executable
production code and tests in the existing project source roots.

| Artifact | Contents |
| --- | --- |
| `requirements.md` | Source inventory, polished requirements, acceptance criteria, scope, clarifications and decisions |
| `architecture.md` | Reuse findings, boundaries, interface contracts, dependencies, implementation work packages |
| `api-contract.md` | Public operations, inputs, outputs, errors, examples, and applicable protocol semantics |
| `traceability.md` | Requirement → acceptance criterion → interface/API operation → test → implementation |
| `execution-log.md` | Stage status, assignments, contract revisions, failures, fixes, validation results, final assessment |

Use stable identifiers such as `REQ-001`, `AC-001-01`, `IF-001`, `API-001`, and
`TEST-001`. Each in-scope requirement must have a verification method. Each
interface and API operation must trace back to an actual requirement. Record
non-testable obligations with an appropriate review or measurement method.

## 1. Collect, analyze, and polish requirements

**Owner:** Requirements analyst; coordinator handles user communication.

**Input:** User-supplied folder containing raw requirements.

1. Ask: “Which folder contains the raw requirements for this task?” Wait for the
   path before analyzing requirements; inspect project context independently.
2. Inventory relevant files recursively, including nested documents and referenced
   local material. Record source paths and sections. Report unreadable formats,
   missing references, or inaccessible files rather than guessing their contents.
3. Extract user goals, functional behavior, actors, inputs, outputs, constraints,
   edge cases, acceptance criteria, and explicit exclusions. Capture performance,
   concurrency, security, and compatibility requirements when supported by sources.
4. Consolidate duplicates and terminology while preserving source references.
   Separate explicit requirements, proposed assumptions, and unresolved questions.
   Do not invent features or silently resolve conflicting source statements.
5. Ask focused clarification questions whenever uncertainty could affect
   correctness, algorithm choice, public API, concurrency, or compatibility.
   Explain the alternatives and recommendation. Continue independent analysis
   while awaiting answers; leave affected decisions pending.
6. Produce a polished specification. Give each requirement a stable ID, a clear
   behavioral statement, source references, and measurable acceptance criteria.
   Record user answers and explicitly identify any remaining low-impact assumptions.

**Result:** A fully prepared requirements list in `requirements.md`.

**Completion criteria:** All relevant sources are accounted for, scope is explicit,
acceptance criteria are verifiable, and all questions blocking design or
implementation are resolved. Proceed without a separate approval ceremony unless
the user has requested one.

## 2. Plan implementation and define contracts

**Owner:** Contract architect, with requirements analyst and test agent review.

**Input:** Prepared requirements and existing project code.

1. Search for equivalent behavior with `rg` or IDE search. Inspect candidates,
   callers, and tests for semantic compatibility. Record what will be reused.
2. Split scope into cohesive interface abstractions at real behavioral boundaries.
   Link each interface and operation directly to requirement and acceptance IDs.
   Keep interfaces small; do not introduce an interface for every class or add
   speculative layers. Internal helpers may remain functions or concrete classes.
3. Specify signatures, input domains, return values, validation, errors/exceptions,
   preconditions, postconditions, invariants, and side effects. Define ownership,
   mutation, ordering, lifecycle, and threading behavior where relevant.
4. Define the public API contract with concrete success, boundary, and failure
   examples. Preserve required signatures and formats. Use the actual API form
   required by the task: Java API, CLI, HTTP, or another boundary. For HTTP, include
   methods, routes, schemas, status codes, and required headers; do not add a web
   layer to a task that does not require one.
5. Create minimal compilable interface/type declarations needed by tests, with no
   production business implementation yet. Define construction or factory seams
   so tests can select mock or production bindings without changing assertions.
6. Create implementation work packages with owners, file boundaries, dependencies,
   reuse choices, expected complexity, and completion criteria. Establish the
   initial contract version and complete the design portion of traceability.

**Result:** An implementation plan, requirement-linked interface abstractions, and
an explicit API contract in `architecture.md` and `api-contract.md`.

**Completion criteria:** Every requirement has a planned implementation boundary
and verification method; contracts are unambiguous and independently testable;
there are no unresolved design blockers or overlapping agent file assignments.

## 3. Create black-box tests using mocked implementations

**Owner:** Test agent; verification agent reviews coverage and independence.

**Input:** Requirements, interface declarations, and the versioned API contract.

1. Build reusable black-box unit/contract suites for every interface. Interact only
   through its public contract and assert observable results, state transitions,
   errors, and side effects. Avoid private internals, concrete implementation
   types, or interaction counts unless those interactions are contractual.
2. Cover every acceptance criterion and applicable boundaries, invalid inputs,
   failure paths, and invariants. Add stateful, concurrency, or resource-lifecycle
   cases only where the contract requires them. Derive expected results from the
   specification, never by copying production logic.
3. Supply deterministic mocked implementations or small contract fixtures for this
   stage. Keep mock behavior separate from test inputs and expected assertions.
   For stateful contracts, use the smallest faithful fixture necessary, not a
   second full production algorithm.
4. Create API integration/contract scenarios through the public entry point using
   mocked internal implementations. Exercise request/response mapping, errors,
   and boundary wiring where available. If the entry point itself is a temporary
   fixture, record that real API integration remains unverified until stage 5.
5. Make test construction replaceable through a small fixture/factory or existing
   injection mechanism. Keep the same test inputs and assertions for real bindings.
6. Execute the mock-backed suites to validate discovery, compilation, fixtures,
   and test mechanics. Record test-to-requirement mappings and review missing
   cases. Explicitly label these results as mock-stage validation.

**Result:** Runnable interface suites and API scenarios with mock bindings and
complete planned acceptance coverage.

**Completion criteria:** Every interface and API operation has applicable success
and failure coverage, every test has a contractual basis, and bindings can be
replaced without rewriting assertions. Passing a programmed mock confirms only
the test setup; it is not evidence of production correctness or real integration.

## 4. Generate the actual implementation

**Owner:** Implementation agents, coordinated by work-package dependencies.

**Input:** Requirements, frozen contract version, black-box tests, work packages,
and the complete project rules.

1. Read and strictly apply
   `/Users/mbugai/work/coding-challenges/.aiassistant/rules/AGENTS.md` before implementing.
   The full file remains authoritative; this plan does not replace its checklist.
2. Implement every planned interface and the required API entry point, adapters,
   and composition wiring. Preserve all contractual inputs, outputs, exceptions,
   invariants, and applicable concurrency guarantees.
3. Reuse suitable project code, then JDK facilities, then existing dependencies.
   Follow SOLID with minimal design and choose the simplest algorithm satisfying
   the input limits. Document relevant time and space complexity.
4. Search for duplicate behavior before and after edits. Keep each business rule
   authoritative in one place and explain intentional separation where required.
5. If dependencies or build plugins must change, follow the rules for current
   official release checks, compatibility, exact versions, advisory review, and
   resolved dependency inspection. Preserve existing build and Mockito settings.
6. Compile and run appropriate existing focused checks for each package. Hand off
   production code and compliance evidence. Do not alter contract assertions to
   accommodate implementation behavior.

**Result:** Complete production implementations and API wiring for the planned scope.

**Completion criteria:** All work packages are integrated, required behavior has
no placeholder implementations, contracts are preserved, and rule review finds
no unresolved violations. Agent-local checks do not substitute for stage 5.

## 5. Replace mocks, run tests, and fix failures

**Owner:** Verification agent runs checks; implementation agents fix assigned defects.

**Input:** Integrated production code and the unchanged contract suites.

1. Replace the mocked subjects in interface suites with generated implementations.
   Unit suites may still isolate collaborators using test doubles at declared
   boundaries. In API integration tests, wire the real entry point and all
   generated in-scope components together; do not mock the behavior being accepted.
2. Keep doubles only for out-of-scope external systems when appropriate. Document
   those boundaries and any real external integration that remains unverified.
   Remove or clearly separate temporary mock-stage bindings from acceptance runs.
3. Preserve requirement-derived test inputs and assertions. Confirm that the
   expected suites are discovered and actually instantiate production components.
4. Run focused suites using `mvn -Dtest=ActualTestClass test`, replacing the
   placeholder with real test classes. Ensure API tests execute under the current
   Maven setup; it uses Surefire, so a name such as `ApiContractTest` is discovered
   without assuming an unconfigured integration-test plugin will run `*IT` tests.
5. Run `mvn test` for final integrated acceptance, including shared-code or build
   changes, plus any relevant configured checks. Record commands, exit codes,
   test totals, failures, errors, skips, and report locations.
6. For every failure, record the requirement ID, failing test, reproducible input,
   expected versus actual behavior, root cause, owner, and fix. Distinguish product
   defects, incorrect tests, wiring defects, environment issues, and evidenced
   pre-existing failures.
7. Fix the cause with the smallest compliant change. Add a focused regression case
   for newly discovered behavior defects where needed. Correct a test only when
   the specification supports the correction, with independent review and a
   recorded reason. Never weaken assertions or exclude tests merely to get green.

**Result:** Production-backed test evidence, fixes, and a classified defect log.

**Completion criteria:** Each observed failure is fixed or explicitly recorded as
a blocker. Any code, test, contract, or binding changes feed the stage 6 loop.

## 6. Repeat validation and repair until all tests are green

**Owner:** Coordinator and verification agent; defect owners perform repairs.

Repeat stage 5 after each repair cycle:

1. Re-run failing tests and relevant neighboring suites against the repaired code.
2. Once focused tests pass, run the complete acceptance suite and required checks
   against the integrated final state. A code change after that run invalidates
   the affected results and requires appropriate revalidation.
3. If a defect exposes a requirements ambiguity, ask the user the focused blocking
   question and continue independent work. If requirements or contracts change,
   update stages 1–3 artifacts and traceability before implementing the revision.
4. Investigate flaky tests instead of rerunning until one pass appears. Stabilize
   fixtures or repair concurrency behavior according to the contract.
5. Review the final diff for accidental changes, unnecessary abstractions,
   duplication, contract violations, and incomplete requirement coverage.

Continue while fixable failures remain. Do not declare success because a retry
limit was reached, a subset passed, tests were disabled, or reports are stale.
If an external blocker prevents progress, record the exact blocker and required
action, mark execution incomplete, and resume after it is resolved.

The full project suite includes `SlowProcessCollectorTest`, which creates
20 million records. If resource limits prevent completion, report exactly what
ran and what did not. Such a run does not satisfy the all-tests-green criterion.

**Result:** All required tests pass against the final production implementation,
with no unresolved acceptance failures or verification gaps.

## Definition of done and final handoff

- The polished requirements are complete and all correctness-blocking questions
  have answers recorded in the clarification log.
- Every requirement traces to a contract, implementation, and passing acceptance
  test or completed non-test verification method.
- Every interface suite exercises its generated implementation; API integration
  tests exercise the composed production components within the defined scope.
- The complete required suite and relevant checks pass on the final code state,
  with no unexpected skips, disabled acceptance cases, or unresolved failures.
- Project-rule review is complete, including reuse, minimal design, duplication,
  applicable complexity and threading analysis, and dependency checks if changed.
- The final report identifies delivered behavior, changed files, reused code,
  relevant complexity, exact validation results and reports, and any remaining
  documented assumptions. Any verification gap means execution is incomplete.

Stop once these criteria are met. Do not expand the scope with speculative
features, unrelated cleanup, or repeated checks without new evidence requiring them.
