---
apply: always
---

# AI assistant rules

Apply these rules to all work in this project. The goal is correct, readable,
minimal interview solutions that the developer can explain and defend.
Explicit challenge requirements and developer instructions take precedence.

## Project context

- Read the challenge, relevant implementation, tests, `README.md`, and `pom.xml` before editing.
- Use JDK 25 and Maven 3.9+ as currently configured; preserve the `src/main` and `src/test` source roots.
- Keep changes within the relevant exercise. Preserve required signatures, input/output formats, and independent challenge submissions.
- Use dependencies from `pom.xml`; do not add bundled JARs to `lib/`.

## Clarify uncertainty before committing to a solution

- Establish inputs, outputs, constraints, edge cases, and acceptance criteria from the prompt and tests.
- If uncertainty could change correctness, the algorithm, public API, concurrency behavior, or dependency compatibility, ask the developer a focused question before implementing the affected part.
- Explain what is unclear, the meaningful alternatives, and your recommendation. Continue independent investigation while awaiting the answer.
- If you cannot justify why the chosen solution meets the constraints after inspecting code and documentation, state the uncertainty and ask; do not hide it behind a confident implementation.
- For low-impact, reversible details such as private names, follow local conventions and proceed. State assumptions that affect observable behavior.
- Do not invent requirements or silently resolve conflicts between the prompt, tests, and implementation.

## Search and reuse before generating code

- Before adding a class, method, algorithm, validation rule, or dependency, search for equivalent behavior with `rg` or IDE symbol/text search.
- Inspect candidate implementations, their callers, and tests to verify semantics, edge cases, and suitability; matching names alone are insufficient.
- Prefer suitable existing project code, then standard-library facilities, then existing dependencies, before writing new code or adding a dependency. Respect exercises that require implementing the algorithm yourself.
- Extend an existing implementation when it owns the same responsibility. Extract shared code only when actual callers share the same contract.
- Keep shared helpers at the narrowest useful scope. Do not couple unrelated exercises or break a standalone submission merely to reuse similar lines.

## Duplication guards

- Before editing, identify existing implementations of the behavior being changed; after editing, search again for copied logic and near-duplicates.
- Keep each shared business rule, invariant, and configuration value in one authoritative place. Update callers instead of copying and modifying an implementation.
- Treat a second implementation of the same rule as a review trigger: reuse or extract it, or explain why the contracts require separation.
- Similar syntax alone does not justify an abstraction. Preserve intentional alternative algorithms and readable test cases; explain intentional duplication when relevant.
- Run an existing duplication checker when available. If automated enforcement is requested, use a Java-compatible detector such as PMD CPD, review existing findings separately, and fail on new unexplained duplication.
- Never hide findings by weakening thresholds or adding blanket exclusions. A manual search is a review guard, not proof that all duplication is absent.

## Apply SOLID with minimal design

- **Single responsibility:** Give each class or module one cohesive responsibility and reason to change. Separate algorithm/domain logic from input/output and infrastructure when both are present.
- **Open/closed:** Use an existing extension point for a required variation. Introduce a new strategy or abstraction only for demonstrated variation; ordinary fixes can modify existing code.
- **Liskov substitution:** Implementations must preserve their contract, including accepted inputs, results, exceptions, and invariants. Avoid inheritance that introduces unsupported operations or stronger preconditions.
- **Interface segregation:** Keep interfaces focused on what their clients need. Do not force callers or implementations to depend on unrelated methods.
- **Dependency inversion:** Keep domain decisions independent of infrastructure details. Pass external collaborators through constructors or parameters, using small contracts at real boundaries; avoid hidden global dependencies.
- Prefer composition over inheritance and pure functions for algorithmic work. A small function or concrete class can be the complete solution.
- Do not create an interface for every class, a dependency injection framework, extra architectural layers, or speculative extension points just to demonstrate SOLID.

## Latest secure dependencies

- Add a dependency only when the required behavior cannot reasonably use existing code or the JDK.
- Whenever adding or updating a dependency or build plugin, verify the latest stable release from its official release documentation and Maven Central at implementation time. Never choose a version from model memory alone.
- Select the latest stable, maintained release compatible with the required JDK and challenge constraints. Check publisher advisories and a current vulnerability database such as OSV or NVD for known vulnerabilities, including transitive dependencies.
- Do not introduce a dependency with a known applicable vulnerability. Find a fixed release or alternative; if neither is viable, explain the blocker and ask the developer.
- Pin exact versions in `pom.xml` properties or an appropriate BOM. Do not use `LATEST`, `RELEASE`, version ranges, snapshots, or prereleases unless explicitly required.
- Inspect the resolved dependency tree after dependency changes and use an available vulnerability scanner. Record sources and the check date; a newer release or clean scan does not guarantee security.
- If the newest release requires a breaking migration or conflicts with an explicit version constraint, explain the conflict and ask the developer before proceeding with that migration or an exception. Flag outdated dependencies encountered without expanding an unrelated task into a repository-wide upgrade.
- If release or advisory checks are unavailable, explicitly report that freshness/security is unverified. Do not invent scan results or describe an unverified dependency as secure.

## Build the smallest complete solution

- Implement only the requested behavior and necessary edge-case handling. Avoid speculative features, generic frameworks, unrelated cleanup, and premature optimization.
- Choose the simplest algorithm that satisfies the stated input limits. Be ready to explain correctness and time/space complexity.
- Match local style, use clear names, and keep comments focused on reasoning or non-obvious invariants.
- For behavior changes, add focused tests for the contract, boundaries, and meaningful failure cases; include a regression test for a bug fix. Avoid tests that merely mirror implementation details.
- Run focused tests with `mvn -Dtest=RelevantTest test`, replacing `RelevantTest` with actual test classes. Run `mvn test` for shared-code or build/dependency changes and any other configured checks relevant to the change.
- The complete suite includes `SlowProcessCollectorTest`, which creates 20 million records. If resource limits prevent running it, report the limitation and which tests ran.
- Inspect the final diff for unnecessary code, duplication, SOLID violations, and accidental changes. Do not claim checks passed unless they actually ran successfully.
- Finish with a brief explanation of the solution, reused code, relevant complexity, tests run, and unresolved assumptions or verification gaps. Stop when the requirement is satisfied.
