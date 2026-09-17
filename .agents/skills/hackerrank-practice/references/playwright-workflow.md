# Playwright HackerRank Workflow

Use only Playwright MCP for browser inspection and interaction. The current chat
owns the browser session for the complete workflow.

## Find and preserve the existing challenge tab

1. Immediately call `browser_tabs` with `action: "list"` in the existing
   Playwright MCP session. Find tabs whose URL has host `www.hackerrank.com` and
   a challenge path, normally `/challenges/<slug>/problem`. The site root alone
   is not a challenge page.
2. Prefer the current challenge tab; otherwise use the sole matching challenge.
   Select its current listed index with `action: "select"` and verify the URL
   and title. If several challenge tabs remain ambiguous, ask which to use.
3. If no challenge is visible, report the observed HackerRank tabs and ask the
   user to open the intended problem in an existing tab. Do not create a tab,
   browser, or context, navigate to the homepage, or start a timed waiting loop.
   If a tool call fails, report its actual error without inferring success.
4. Record the selected tab index, full URL, title, and problem slug. Re-list tabs
   and verify the recorded challenge before editor entry because indices can
   change. If it disappears, report the missing target; do not restart MCP or
   create a replacement session.
5. Keep all browser work on the selected challenge. Do not call
   `browser_navigate`, `browser_navigate_back`, close or create a tab, or follow
   links away from the exercise. Phase handoffs do not restart tab discovery.
6. Add no countdowns, sleeps, fixed-duration `browser_wait_for` calls, or
   artificial pauses. Await actual tool completion and use observable readiness
   or result conditions when the page is loading or executing code. Preserve
   the required `slowly: true` character-by-character editor input.

## Read the complete challenge

- Start with `browser_snapshot`. Use a bounded-depth snapshot or `browser_find`
  for headings when the full tree is too large, then capture the relevant
  subtrees. Enumerate every statement heading and read each section through its
  end, including Function Description, Parameters, Returns, Input Format,
  Constraints, Output Format, every Sample Input and Sample Output, and every
  Explanation when present.
- Scroll only as needed to trigger lazy-rendered statement content. Re-snapshot
  after scrolling. Clicking an existing Problem tab or a collapsed statement
  disclosure is allowed when required to reveal the statement; do not change the
  challenge or open another page.
- Always ask the user which Java version to use before capturing starter code
  or beginning the coding phases, unless they have already explicitly supplied
  it for this exercise. Do not parse the selected language to make that choice.
  Visible Java options can help the user choose, but do not delay the question
  to enumerate options or inspect version-related DOM attributes.
- Select the user-chosen version using the language control and confirm the
  corresponding starter code has loaded. Reading the control to locate or
  confirm that exact option is allowed; it must not replace the user's choice.
  If the option is unavailable or selection fails, report the blocker.
- Record the chosen release, selected HackerRank label, and resolution source
  `user` in `task.md` and all phase handoffs. Read the complete starter without
  altering it. If accessibility state omits the code, use read-only
  `browser_evaluate` against the editor DOM. Do not use
  `browser_run_code_unsafe` to extract or set editor content.
- Use `browser_take_screenshot` with an exact image/diagram target and a filename
  under `src/main/bmv/<task_name>/` for a visual that carries problem meaning.
  Inspect the returned artifact path and verify the file exists at the intended
  repository path before adding its relative Markdown link. Decorative site
  imagery does not belong in the package.
- Cross-check section headings and counts of examples/constraints after capture.
  A viewport screenshot alone is not proof that the full statement was read.

## Entry choice after development

After the coding sequence and acceptance checks finish, link the editor-ready
file and ask whether the user wants to paste it manually or have Playwright
enter it and run tests. Await an explicit answer before editing the browser.
Manual entry ends the skill immediately after delivering the source and local
results. Do not monitor manual entry or click Run Code. Only the Playwright
choice enables the following entry, Run Code, and repair steps.

## Enter code as human typing (Playwright choice only)

1. Confirm the recorded page identity and resolved Java version. If the
   language or starter contract changed since capture, stop and reconcile
   `task.md`, the contracts, tests, and editor-ready source before editing.
2. Click the code editor using an exact snapshot target. Press `ControlOrMeta+A`
   and then `Backspace` to clear it.
3. Call `browser_type` on the focused editor with the complete source and
   `slowly: true`. This is the required character-by-character input mechanism.
   Do not use `browser_fill_form`, clipboard paste, DOM assignment, JavaScript
   injection, or `browser_run_code_unsafe`.
4. Read the editor content back through accessibility state or read-only DOM
   evaluation and compare it with the local editor-ready source before running.
   Correct truncation or transcription errors by retyping.

## Run and inspect visible tests (Playwright choice only)

1. Capture the pre-run button state and existing results status/content. Locate
   the exact **Run Code** button in a fresh snapshot and click it. Never target a
   generic neighboring button.
2. Require evidence of a new execution: observe a running transition, a Run Code
   disable/enable cycle, a changed result identifier/content, or another page
   state that could only follow this click. A results panel that was already
   visible before the click is not sufficient. Wait for the new execution to
   finish, then take a fresh snapshot.
3. Inspect the overall status and every visible case or result panel. Expand
   visible failed-case details when needed. Capture compiler diagnostics, stderr,
   runtime errors, input, expected output, and actual output that HackerRank
   exposes.
4. Count an attempt only after an execution result appears. A transport error,
   authentication prompt, rate limit, or indefinitely running job is an external
   blocker to diagnose and report; it is not a test failure or a pass.
5. All visible cases must pass in the same final run. Never claim hidden tests
   pass because Run Code succeeds.

At no point click, focus-and-press, script, or otherwise invoke **Submit**. Stop
after the final passing Run Code inspection.
