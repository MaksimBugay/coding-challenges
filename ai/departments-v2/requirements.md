# Department hierarchy requirements

## Status

- Phase: development complete
- Contract version: 2
- Requirements source: `src/main/bmv/wrike/departments/src/main/java/com/interview/`
- Correctness blockers: none

Version 2 is a fresh design from the supplied sources. It does not rely on the
unsupported developer clarification recorded in the pre-existing
`ai/departments/` draft.

## Source inventory

| Source | Relevant content |
| --- | --- |
| `requirents.txt`, lines 1-2 | Implement `DepartmentService.isUserAllowed`; review, repair, and report issues in `DepartmentService`. |
| `DepartmentService.java`, class Javadoc and methods | Three move rules, the required public operation, DAO interaction, and the incomplete authorization method. |
| `Department.java` | A mutable department has one `parentId`, one `managerId`, an ID, and a title. |
| `DepartmentDao.java` | Lookup by primitive ID and non-transactional save operations. |
| `task-img.png`, upper rules section | Repeats the three hierarchy rules. |
| `task-img.png`, questions/notes section | Multiple roots are allowed, a child has at most one parent, loops are forbidden, concurrent updates must be considered, the request DTO contains department and new-parent IDs, and the endpoint must be protected. |
| Nested `README.md` | The exercise includes code review and implementation. |
| Nested `pom.xml` | Standalone Java 17 build with Spring Beans 6.0.12. |
| Root `AGENTS.md`, `README.md`, and `pom.xml` | The active repository build uses JDK 25, Maven 3.9+, the existing source roots, JUnit, and Mockito. |

All eight files under the supplied exercise were inventoried. The PNG is readable
at 752 by 361 pixels (SHA-256
`d22a1360229ebd1a7ad8db5f5aae0f6dd272a17a8507c5ec52bf8b3cf3a27ae1`).
There are no nested instructions, tests, callers, or referenced external files.

## Functional requirements

### REQ-001 - Determine management authority

`isUserAllowed(department, managerId)` returns `true` when `managerId` equals the
manager of `department` or any ancestor reached by following `parentId`.

- **AC-001-01:** The department's direct manager is allowed.
- **AC-001-02:** A manager of its parent or any higher ancestor is allowed.
- **AC-001-03:** A manager absent from that ancestry is denied.

Sources: `requirents.txt` line 1; service/image rule 1 and rule 3.

### REQ-002 - Authorize both sides of a move

The acting manager must satisfy REQ-001 for both the department being moved and
the proposed new parent.

- **AC-002-01:** Failure on either side returns `false`, leaves the target
  unchanged, and does not save it.

Sources: service/image rules 1 and 3.

### REQ-003 - Preserve root or child status

A root cannot become a child and a child cannot become a root.

- **AC-003-01:** A target with no parent cannot be moved under another department.
- **AC-003-02:** A child cannot be moved to a null parent.
- **AC-003-03:** Either rejection returns `false` without mutation or save.

Sources: service/image rule 2.

### REQ-004 - Preserve a loop-free, single-parent hierarchy

More than one root may exist. Every child has at most one parent, represented by
its scalar `parentId`. A move must not make the target its own ancestor.

- **AC-004-01:** Moving a department under itself is rejected.
- **AC-004-02:** Moving a department under one of its descendants is rejected.
- **AC-004-03:** Rejections return `false` without mutation or save.
- **AC-004-04:** Valid moves do not depend on there being one global root.

Source: `task-img.png`, questions 1-3.

### REQ-005 - Persist an allowed move

After every rule passes, update only the target's `parentId`, pass that target to
`DepartmentDao.save` exactly once, and return `true`.

- **AC-005-01:** A permitted move returns `true`, changes the parent ID to the
  requested ID, retains all other target fields, and saves the target once.

Sources: existing `moveDepartment` operation and the three move rules.

### REQ-006 - Fail safely for unusable request or lookup data

The service must not turn malformed input into an unintended write.

- **AC-006-01:** A null request ID or manager ID returns `false` without mutation
  or save; a null new-parent ID specifically implements REQ-003.
- **AC-006-02:** A missing target or proposed parent returns `false` without
  mutation or save.
- **AC-006-03:** A missing ancestor or repeated department ID during traversal
  returns `false` without mutation or save instead of looping indefinitely.

This is a proposed defensive interpretation of "loops are not allowed" and the
boxed public parameters. The supplied sources define no exception contract.

### REQ-007 - Identify concurrency limits

Authorization, cycle validation, mutation, and persistence must use one stable
hierarchy state for concurrent correctness.

- **AC-007-01:** The review reports that separate `findById` and `save` calls do
  not guarantee this property.
- **AC-007-02:** The handoff states the persistence guarantee needed in a real
  deployment: a transaction or equivalent lock/optimistic validation covering
  every department read and the final write.

Source: `task-img.png`, "Concurrent updates". The source gives no isolation,
locking, version, retry, or failure semantics, so version 2 reports this boundary
instead of inventing an incompatible DAO API.

### REQ-008 - Keep caller authentication outside the DTO

The external request contains the department ID and new-parent ID. The acting
manager identity must come from a protected, authenticated caller context rather
than client-controlled DTO data.

- **AC-008-01:** The review records this trust-boundary requirement.
- **AC-008-02:** The existing service may accept `managerId` only as a trusted
  value supplied by that protected boundary.

Source: `task-img.png`, DTO and protected-endpoint notes. No endpoint framework,
authentication provider, or DTO declaration is supplied, so no endpoint is added.

### REQ-009 - Review and repair the supplied service

- **AC-009-01:** Development removes the `isUserAllowed` placeholder and fixes
  issues required by REQ-001 through REQ-006 without changing the public API.
- **AC-009-02:** Concurrency, authentication, and build/dependency findings are
  reported with their practical limits.
- **AC-009-03:** The declarations compile in the active root Maven build.

Sources: `requirents.txt` line 2 and nested `README.md`.

## Review findings

| ID | Existing issue | Required disposition |
| --- | --- | --- |
| ISSUE-001 | `isUserAllowed` always returns `false`. | Implement under REQ-001. |
| ISSUE-002 | The service does not traverse parent departments. | Implement under REQ-001. |
| ISSUE-003 | Boxed null IDs are auto-unboxed by `findById(long)`. | Guard under REQ-006. |
| ISSUE-004 | Missing DAO results are dereferenced. | Guard under REQ-006. |
| ISSUE-005 | Child-to-root handling is absent and a null parent is looked up first. | Fix under REQ-003/REQ-006. |
| ISSUE-006 | Self-parenting and descendant moves can create loops. | Fix under REQ-004. |
| ISSUE-007 | Malformed stored cycles can make ancestry traversal infinite. | Detect under REQ-006. |
| ISSUE-008 | Validation and save are not atomic under concurrent updates. | Report under REQ-007; persistence support is missing. |
| ISSUE-009 | `managerId` is an ordinary method argument and could be caller-controlled at an unprotected boundary. | Report and enforce the REQ-008 integration rule. |
| ISSUE-010 | The nested POM targets Java 17 and includes Spring Beans although current source uses constructor injection and no Spring API. | Report; do not alter dependencies during this challenge phase. |
| ISSUE-011 | The mutable entity and void `save` expose no version or conflict result. | Report with ISSUE-008. |

## Scope, exclusions, and assumptions

In scope: ancestry authorization, move validation, loop prevention, safe failure,
target mutation/save behavior, the supplied service review, and explicit reporting
of concurrency/authentication integration requirements.

Out of scope: implementing persistence, transactions, locks, HTTP endpoints,
authentication, DTOs, department creation/deletion, and changing manager/title.
DAO runtime failures propagate. Log wording and count are not contractual.

- **ASM-001:** `findById` returns null when an entity is absent.
- **ASM-002:** DAO data is stable during a unit-level service call; production
  integrations must satisfy REQ-007 separately.
- **ASM-003:** Moving to the already-current parent is unspecified because the
  sources do not define whether it is a successful no-op.
