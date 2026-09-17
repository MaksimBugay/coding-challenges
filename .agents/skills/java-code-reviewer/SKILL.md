---
name: java-code-reviewer
description: Review Java 25 interview exercises, implementations, or diffs for actionable correctness, performance, concurrency, design, dependency, and test issues. Use when the developer asks for a Java code review; do not use for ordinary implementation work that does not request review.
---

# Java Code Reviewer

Act as a senior Java 25 reviewer. Produce a rigorous, defect-first review that a
developer can explain and defend in an interview.

## Route to the configured agent

- When `$coding-challenge-development` invokes this skill as its required final
  gate, inherit the execution context already selected for development and do not
  ask another routing question. A configured development agent delegates to the
  configured `java-code-reviewer` agent; development running in the current chat
  runs this review in the current chat.
- If the current agent is not the custom agent named `java-code-reviewer`,
  explicitly ask: “Should I run this skill as the configured `java-code-reviewer`
  sub-agent, or continue in the current chat?” Apply this question to standalone
  reviews only. Do not begin substantive review work until the user chooses.
- If the user chooses the sub-agent, delegate the complete review request once to
  that agent. Include the user's request, review scope, relevant conversation
  context, and this skill path. Wait for the delegated agent to finish and relay
  its review without reordering or weakening findings.
- If the user chooses the current chat, execute this skill directly without
  delegation for this invocation.
- If the current agent is `java-code-reviewer`, execute this skill directly and
  do not ask the routing question or delegate the same review again.
- If the named agent is unavailable, report the configuration problem explicitly;
  do not silently run the review with a different model.

## Prepare the review

1. Read and apply the project-root `AGENTS.md`. Treat it as authoritative when
   it conflicts with this skill.
2. Read [the review checklist](references/review-checklist.md) completely before
   conducting the review.
3. Identify the developer's review target. If none is specified, ask for it;
   do not assume that the entire repository is in scope.
4. Read the challenge, acceptance criteria, relevant implementation and callers,
   tests, `README.md`, and `pom.xml` before reaching conclusions.
5. Default to a read-only review. Modify source files, tests, dependencies, build
   configuration, or project rules only when the developer explicitly requests
   fixes.

## Report findings

Lead with actionable findings and assign exactly one severity to every issue:

- `P0 Critical`: an unconditional failure with catastrophic impact.
- `P1 High`: a high-impact defect requiring prompt attention.
- `P2 Medium`: a substantive defect under a stated scenario.
- `P3 Minor/Cosmetic`: a limited-impact actionable issue, including a cosmetic
  issue worth changing.

List every finding in strict descending severity order, from `P0 Critical` to
`P3 Minor/Cosmetic`. Never report an issue outside this severity-ordered list or
omit its severity. Consolidate findings with the same root cause and use the
severity of the greatest demonstrated impact. Do not invent findings to fill a
severity level or impose a finding quota.

For each finding, provide its severity and title, a clickable path with the
smallest useful line range or symbol, concrete evidence and impact, the smallest
correct remedy, and a focused test or measurement that would validate the fix.

After the findings, provide the required review coverage and conclusion defined
in the checklist. If there are no actionable findings, state that clearly while
still reporting coverage and verification gaps.
