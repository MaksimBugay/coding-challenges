---
name: hackerrank-practice
description: Capture an open HackerRank problem with Playwright, run the complete Java coding-challenge workflow in the current chat, ask the user for the Java version and for manual or automated solution entry, and never submit.
---

# HackerRank Practice

Complete one HackerRank exercise from the problem page already open in the
Playwright MCP session. Preserve the selected page throughout the workflow and
finish when the user chooses manual entry or every visible automated Run Code
test passes. Never click Submit.

Invocation authorizes the design, testing, review, and development sequence in
this chat. Nested skills' routing and phase-handoff questions are already
answered: continue directly in this chat. Two explicit user choices remain:
ask which Java version to use before coding, and ask about manual versus
Playwright entry after development passes acceptance. Browser editor entry and
Run Code are authorized only when the user chooses Playwright automation.

## Execute entirely in the current chat

- The current agent performs every Playwright action and every design, testing,
  Java review, development, repair, and validation step in this chat.
- Do not call `spawn_agent`, delegate to a configured agent, or start parallel
  work at any point in this workflow.
- For `$coding-challenge-solution-design`, `$coding-challenge-testing`,
  `$java-code-reviewer`, and `$coding-challenge-development`, bypass their
  standalone routing questions and execute each skill directly in this chat.
- Preserve the same chat context, Playwright MCP session, task package, and
  `ai/<task_name>/` handoff across all phases.

## Establish the browser and repository context

Read [the Playwright workflow](references/playwright-workflow.md) completely
before interacting with the browser.

1. Read the project-root `AGENTS.md`, applicable nested instructions,
   `README.md`, and `pom.xml`. Inspect scoped local changes and preserve unrelated
   user work.
2. Immediately list existing tabs with Playwright MCP `browser_tabs` and
   `action: "list"`. Find the open challenge on `https://www.hackerrank.com/`
   using the selection procedure in the Playwright reference. Select its listed
   index with `action: "select"`. Prefer the active challenge; otherwise use the
   sole matching challenge. Ask only if no challenge is visible or the target is
   ambiguous. Do not create a tab, browser, or context, or restart the session.
3. Record the selected tab index, URL, problem slug, and title. A valid target is
   a HackerRank problem page, normally containing `/challenges/<slug>/problem`.
   Keep all later browser work on this exact page. Never navigate to another URL.
4. Proceed without countdowns, fixed-duration waits, sleeps, or pauses between
   phases, apart from the required user choices. Await actual tool completion
   and observable page readiness or execution results. Automated editor entry
   must still type character by character.
5. Explicitly ask: “Which HackerRank Java version should I use?” Wait for the
   user's answer before capturing starter code, creating contracts, or starting
   a coding phase. A version explicitly supplied by the user for this exercise
   already answers this question; do not ask twice. Never select a version by
   parsing the current selection, starter syntax, or repository JDK. If Java
   options are readily visible, include them in the question; do not make
   version discovery or DOM parsing a prerequisite to asking. Select the
   user-chosen option through the language control and confirm its starter code
   has loaded. If that option is unavailable or cannot be selected, report the
   concrete blocker rather than choosing a substitute. Record the user's Java
   release and the selected HackerRank label, with resolution source `user`.
6. Read the complete problem statement, input and output formats, every
   constraint, all examples and explanations, and the complete starter code for
   the resolved Java version. Expand or scroll problem content only when needed to
   expose existing material. Do not infer text hidden behind an authentication,
   loading, or rendering failure.
7. Inspect statement images and diagrams. Save only images needed to understand
   the contract by taking element screenshots with Playwright. Verify each
   returned screenshot path exists inside the new package before linking it from
   Markdown. Do not recreate a diagram from memory when the page provides it.

## Create the raw challenge package

Derive a concise lowercase snake-case Java package name from the HackerRank
problem title, for example `matrix_layer_rotation`. Create a new directory at
`/Users/mbugai/work/coding-challenges/src/main/bmv/<task_name>/` (the project-relative
path is `src/main/bmv/<task_name>/`). Search first for an existing package or
handoff for the same problem. Do not overwrite an ambiguous or unrelated existing exercise;
ask for direction if the intended target is unclear.

Write `src/main/bmv/<task_name>/task.md` as the authoritative raw challenge. It
must contain:

- HackerRank title and canonical page URL;
- the complete statement in clear Markdown, preserving mathematical meaning;
- input format, output format, constraints, examples, and explanations;
- local links to each required captured image;
- user-chosen Java release and selected HackerRank label, with resolution
  source `user`, plus the exact starter code loaded for that
  version in a fenced block;
- required function/class signatures and observable input/output behavior;
- an algorithm analysis justified by the constraints, including invariants,
  edge cases, and time and auxiliary-space complexity;
- any unreadable, ambiguous, or apparently conflicting source material.

Keep factual problem text distinguishable from analysis. Compare the Markdown
against the browser source once more before treating it as complete.

## Run the blocking coding-challenge chain

Use `ai/<task_name>/` for phase handoffs and the new package as the implementation
target. The first phase is the project's design skill, whose exact registered
name is `$coding-challenge-solution-design`. Run this exact chain directly and
serially in the current chat:

`$coding-challenge-solution-design` → `$coding-challenge-testing` →
`$coding-challenge-development` → ask about manual or Playwright solution entry.

Complete each phase and its acceptance checks before immediately starting the
next phase; do not insert a delay or ask for phase-handoff confirmation.

1. Execute `$coding-challenge-solution-design` directly with `task.md`, captured
   assets, package and handoff paths, editor starter code, and the resolved Java
   version. Complete its design documents and compilation gate, then continue
   immediately to testing without asking permission.
2. Execute `$coding-challenge-testing` directly from the completed design. Pass
   the same Java version. Complete the testing gate, then continue immediately to
   development without asking permission.
3. Execute `$coding-challenge-development` directly from the completed design
   and testing handoffs. Pass the same Java version, finish implementation and
   acceptance, then execute its required `$java-code-reviewer` review and repair
   loop on the production solution classes directly in this chat. Ask the required
   entry-choice question below only after the final review has no actionable
   findings and all repairs pass the required validation.

Preserve unrelated repository edits and each phase's file ownership boundaries.
If a phase uses a different Java version, correct the handoff and rerun affected
validation before continuing. Never overlap phase work.

Treat boundaries between the three coding skills as internal completion gates.
The entry-choice question after development is required.
Continue automatically when the phase is ready. Stop and report the exact
blocker only if a phase is incomplete, a correctness question genuinely requires
the user, or its acceptance criteria fail. Do not bypass a phase or weaken its
tests.

Development must leave an editor-ready solution that matches the captured
HackerRank starter contract and selected language. Before browser entry, inspect
the implementation file, confirm no design placeholder remains, and confirm the
production-bound tests and required development checks passed. If any of these
conditions is missing, complete the remaining development checks; do not type
partial or proposed code into HackerRank. Keep any HackerRank-only adapter minimal and
separate from reusable algorithm code when the local build requires a different
entry point.

## Ask how to enter the finished solution

After development and its acceptance checks are complete, provide a link to the
verified editor-ready source and explicitly ask:

“Do you want to paste the solution manually, or should I enter it automatically
via Playwright MCP and run the tests?”

Wait for the answer. Do not default to automation, change the editor, or click
Run Code while this choice is pending.

- **Manual:** provide the editor-ready file and local validation summary, record
  the manual handoff in `execution-log.md`, and finish skill execution
  immediately. Do not enter code, run browser tests, monitor the user's paste,
  or wait for them to finish. Report HackerRank results as unverified.
- **Playwright:** continue with the automated entry and validation procedure
  below, including repairs until every visible test passes. Never submit.

## Enter and validate the solution (Playwright choice only)

Load the verified editor-ready source from the repository. Re-list browser tabs
and select the exact recorded HackerRank page if needed. Confirm its URL,
problem title, and the user-chosen language before touching the editor.

Follow the editor and Run Code procedure in the Playwright reference. Type the
complete editor-ready solution with Playwright's human-like slow typing, one
character at a time. Do not inject code with JavaScript, set DOM values, use the
clipboard, or use a bulk form-fill action.

Click **Run Code**, wait for execution to finish, and inspect every visible test
result, compiler message, runtime error, expected value, and actual value. If a
visible test fails:

1. record the evidence and diagnose the root cause against `task.md` and the
   versioned contracts;
2. fix the local production implementation and focused regression tests;
3. run the affected local tests and update handoff evidence;
4. regenerate the editor-ready code and replace the editor contents with slow
   character-by-character typing;
5. click **Run Code** again and inspect all visible results.

Continue until all visible tests pass. Do not retry unchanged code. If the same
failure remains after evidence-based fixes, inspect the captured contract,
language/starter compatibility, and editor contents before another attempt. Stop
only for a concrete external blocker or correctness ambiguity that cannot be
resolved from the page and repository.

## Finish

Never click **Submit**, invoke a submit control, or trigger submission through
keyboard shortcuts or script. Passing Run Code results do not authorize a
submission. The user will submit manually.

For either entry choice, report the package and handoff paths, editor-ready
source, user-chosen Java version, algorithm and complexity, and local validation.
For manual entry, finish at the handoff and state that HackerRank tests were not
run by this workflow. For automated entry, also report the tab used, Run Code
attempt count, and final visible results; stop after all visible cases pass.
Never claim hidden tests were verified.
