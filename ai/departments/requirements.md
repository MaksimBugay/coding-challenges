# Department hierarchy requirements

## Status

- Phase: solution design blocked pending Q-001
- Contract version: 1
- Requirements source: `src/main/bmv/wrike/departments/src/main/java/com/interview/`
- Correctness blockers: endpoint/security and concurrent-update scope in the newly supplied image

## Source inventory

| Source | Relevant content |
| --- | --- |
| `src/main/bmv/wrike/departments/src/main/java/com/interview/requirents.txt`, lines 1-2 | Implement `DepartmentService.isUserAllowed`; review and repair `DepartmentService` and report issues. |
| `src/main/bmv/wrike/departments/src/main/java/com/interview/DepartmentService.java`, class Javadoc | Three hierarchy rules for changing a department, preserving root/child status, and authorizing the new parent. |
| `src/main/bmv/wrike/departments/src/main/java/com/interview/Department.java` | Mutable department data: ID, parent ID, manager ID, and title. |
| `src/main/bmv/wrike/departments/src/main/java/com/interview/DepartmentDao.java` | Lookup by ID and save operations. |
| `src/main/bmv/wrike/departments/src/main/java/com/interview/task-img.png` | Repeats the three hierarchy rules; states that multiple roots are allowed, each child has one parent, and loops are forbidden; also notes concurrent updates, a `(departmentId, newParentId)` DTO, and a protected endpoint without defining their contracts. |
| `src/main/bmv/wrike/departments/README.md` | The exercise requires both review and implementation. |
| `src/main/bmv/wrike/departments/pom.xml` | Original standalone Java 17 build and Spring Beans dependency. |
| `AGENTS.md`, root `README.md`, root `pom.xml` | Active repository build uses JDK 25, Maven 3.9+, root `src/main` and `src/test`, JUnit, and Mockito's configured Java agent. |
| Developer clarification, 2026-09-15 | Approved ancestor-based authorization, `false` for invalid/missing inputs, root/child preservation, self/descendant rejection, and safe failure on malformed hierarchy cycles. |

No nested instructions, challenge tests, callers, or other referenced material exist under the supplied requirements folder. The filename `requirents.txt` is retained as supplied.

## Functional requirements

### REQ-001 — Determine management authority

`isUserAllowed(department, managerId)` must return `true` exactly when the non-null manager ID equals the manager ID of the supplied department or of any department reached by repeatedly following `parentId`.

- **AC-001-01:** The department's direct manager is allowed.
- **AC-001-02:** A manager of any ancestor, including the root ancestor, is allowed.
- **AC-001-03:** An unrelated manager is denied.
- **AC-001-04:** A null department or null manager ID is denied.
- **AC-001-05:** A missing ancestor or a repeated department ID in the traversed hierarchy is denied rather than looping or throwing.

Source: `requirents.txt` line 1; `DepartmentService` Javadoc rules 1 and 3; developer clarification.

### REQ-002 — Authorize the department being moved

A move may proceed only when REQ-001 allows the acting manager for the target department.

- **AC-002-01:** An unauthorized target returns `false`, remains unchanged, and is not saved.

Source: `DepartmentService` Javadoc rule 1.

### REQ-003 — Preserve root/child status

Moving a root under another department and moving a child to the root are both forbidden.

- **AC-003-01:** A target whose `parentId` is null cannot be moved and produces `false` without a save.
- **AC-003-02:** A null `newParentId` produces `false` without a lookup using that null ID, mutation, or save.

Source: `DepartmentService` Javadoc rule 2; developer clarification.

### REQ-004 — Authorize the new parent

A move may proceed only when REQ-001 allows the acting manager for the resolved new parent.

- **AC-004-01:** An unauthorized new parent returns `false`; the target remains unchanged and is not saved.

Source: `DepartmentService` Javadoc rule 3.

### REQ-005 — Preserve a valid department tree

The target cannot become its own parent or be moved below one of its descendants. A malformed new-parent ancestry must fail safely.

- **AC-005-01:** `departmentId == newParentId` returns `false` without mutation or save.
- **AC-005-02:** A new parent whose ancestry contains the target returns `false` without mutation or save.
- **AC-005-03:** A missing ancestor or repeated ID while inspecting the new-parent ancestry returns `false` without mutation or save.

Source: `task-img.png`; developer clarification.

### REQ-006 — Persist an allowed move

After every rule passes, set the target's `parentId` to `newParentId`, save that target exactly once, and return `true`.

- **AC-006-01:** A valid move returns `true`, mutates only the target's parent ID, and passes the same target object to one `save` call.

Source: existing public operation and Javadoc rules. The expected result follows from the rules, not from the known-incomplete implementation.

### REQ-007 — Reject invalid lookup inputs and missing entities

- **AC-007-01:** A null department ID, null new-parent ID, or null manager ID returns `false` without mutation or save.
- **AC-007-02:** An unknown target or new-parent ID returns `false` without mutation or save.

Source: developer clarification.

### REQ-008 — Review and repair the supplied service

The implementation phase must correct the documented issues below while preserving the public API in [api-contract.md](api-contract.md).

- **AC-008-01:** The implementation compiles in the active repository build and contains no incomplete authorization placeholder.
- **AC-008-02:** Each finding is fixed when required by REQ-001 through REQ-007 or explicitly reported as outside the contract.

Source: `requirents.txt` line 2 and the Department Project README.

## Review findings

| ID | Finding | Required disposition |
| --- | --- | --- |
| ISSUE-001 | `Department` and `DepartmentDao` declared a path-derived package inconsistent with the required `com.interview` API. | Fixed in the design declaration. |
| ISSUE-002 | Field injection used unresolved `@Autowired`; it did not compile and hid the required DAO collaborator. | Replaced by explicit constructor injection in the design declaration. |
| ISSUE-003 | `isUserAllowed` always returns `false`. | Implement under REQ-001 in development. |
| ISSUE-004 | Null boxed IDs are auto-unboxed for `findById(long)`, causing exceptions. | Fix under REQ-003 and REQ-007. |
| ISSUE-005 | Missing DAO results are dereferenced. | Fix under REQ-007. |
| ISSUE-006 | The child-to-root rule is not implemented. | Fix under REQ-003. |
| ISSUE-007 | Self-parenting and descendant moves can create cycles. | Fix under REQ-005. |
| ISSUE-008 | Hierarchy traversal can encounter missing ancestors or pre-existing cycles. | Detect and deny under REQ-001 and REQ-005. |
| ISSUE-009 | The nested Maven file targets Java 17 and retains Spring Beans solely for the removed field annotation, while the active repository build is JDK 25. | Reported; no nested build migration is required for the root build or contract. |
| ISSUE-010 | No transaction or locking boundary guarantees an atomic hierarchy snapshot. | Reported as outside the supplied contract. |

## Scope and exclusions

In scope: authorization through parent links, move validation, target mutation/save behavior, cycle safety, compilable declarations, and the requested code-review report.

Out of scope: department creation/deletion, changing managers or titles, persistence implementation, logging text, transactions, concurrent updates, and repairing the nested standalone Maven build. DAO runtime failures propagate; tests do not simulate infrastructure failures. Behavior for an already-current parent is unspecified because the sources do not say whether a no-op counts as a move.

The endpoint, DTO, security mechanism, and concurrent-update behavior are provisional exclusions pending Q-001.

## Decisions and assumptions

- **DEC-001:** `com.interview` is the required package, based on the explicit method name and directory path.
- **DEC-002:** The root JDK 25 Maven build is authoritative for this repository exercise.
- **DEC-003:** `DepartmentDao.findById` returns null for an unknown ID in contract fixtures.
- **DEC-004:** A single service call assumes DAO results describe one stable hierarchy snapshot. Concurrent mutation is unspecified.
- **DEC-005:** IDs on departments returned for valid traversal are non-null and correspond to their lookup keys. A violation is malformed data and must be denied if encountered during traversal.

## Unresolved question

- **Q-001:** Does `task-img.png` require an HTTP endpoint, request DTO, endpoint security, and a concurrency guarantee, or are those review/interview discussion notes? The supplied code and textual task define only `DepartmentService`, contain no web/security boundary, and give no concurrency semantics. The recommended interpretation is to keep them as reported considerations outside contract version 1.
