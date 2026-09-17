# Department hierarchy architecture

## Status

- Phase: development complete
- Contract version: 2
- Design blockers: none
- Production business behavior remains incomplete pending development

## Reuse findings

Repository searches found no other department hierarchy traversal, caller, or
test. The supplied `Department`, `DepartmentDao`, and `DepartmentService` already
form the smallest useful boundary. JDK `Objects.equals` and `HashSet<Long>` cover
value comparison and cycle detection; no new production dependency or interface
is justified.

## IF-001 - Service boundary

```java
package com.interview;

public class DepartmentService {
    public DepartmentService(DepartmentDao dao);
    public boolean moveDepartment(Long departmentId, Long newParentId, Long managerId);
    protected boolean isUserAllowed(Department department, Long managerId);
}
```

Construction requires a non-null DAO. The current concrete service and signatures
are preserved. The protected authorization operation remains accessible to tests
in package `com.interview` without widening the production API.

## IF-002 - Persistence seam

```java
package com.interview;

public interface DepartmentDao {
    Department findById(long id);
    void save(Department department);
}
```

`findById` supports target, proposed-parent, and ancestor reads. `save` receives
the moved target. A test binding uses a map-backed DAO that records reads and
saves. A production binding supplies its real DAO implementation.

The interface has no transaction, lock, version token, or conflict result.
Consequently it cannot prove REQ-007 under concurrent writers. A real adapter must
execute all reads and the save in one transaction with serializable behavior,
explicit locks, or optimistic validation of every observed node. Choosing one
requires datastore information absent from the challenge.

## IF-003 - Protected caller boundary

The image describes an external DTO containing only `departmentId` and
`newParentId`. An integration layer must authenticate the caller, derive a trusted
manager ID, then invoke IF-001. No endpoint or DTO declaration is introduced
because no web framework, transport, authentication provider, or route contract
is supplied.

## Contract version 2 design

Authorization follows `parentId` from the starting department toward its root,
comparing manager IDs by value. A visited-ID set terminates malformed cycles.
Missing nodes, null required values, and malformed traversal fail closed.

Move validation resolves the target and proposed parent, authorizes both, rejects
a root target or null proposed parent, and walks the proposed parent's ancestry to
reject self/descendant moves. Mutation and the one save occur only after all reads
and validations succeed. A shared private ancestry walker may centralize missing
node and cycle handling; no new public abstraction is needed.

For hierarchy height `h`, the expected service cost is `O(h)` DAO reads/time and
`O(h)` auxiliary space. The scalar `parentId` preserves the one-parent model.
Multiple roots require no global-root lookup.

## Contract-test construction seam

Testing owns these planned files under `src/test/com/interview/`:

- `DepartmentServiceContract.java`: shared scenario inputs and assertions.
- `MockDepartmentServiceContract.java`: scenario-ID keyed expected observations,
  independent from production logic.
- `DepartmentServiceContractTest.java`: map-backed DAO production binding.

The shared suite invokes a test-scoped `Operation` returning an immutable
`Observation` containing the boolean result, target state, save count, saved
object identity, and whether every non-target department remained unchanged.
Authorization-only scenarios use a package-local adapter around
the protected method. Mock responses are literal values from `api-contract.md`;
the mock never calls production code. The production binding constructs fresh
department graphs for each scenario and invokes the exact IF-001 methods.

Planned commands:

- `mvn -DskipTests compile`
- `mvn -Dtest=MockDepartmentServiceContract test`
- `mvn -Dtest=DepartmentServiceContractTest test`
- `mvn test` after development, including `SlowProcessCollectorTest`

## Work packages

### WP-001 - Fresh contract and declaration validation

- Owner: solution-design phase.
- Contract: version 2.
- Permitted files: `ai/departments-v2/` only; existing declarations are read-only.
- Completion: five handoff documents exist, required signatures already compile,
  and every requirement has a boundary and verification.

### WP-002 - Independent black-box tests

- Owner: testing phase.
- Contract: frozen version 2.
- Permitted files: the three planned test files and v2 status/evidence sections.
- Dependency: WP-001.
- Completion: TEST-001 through TEST-020 pass against independently declared mock
  responses; the production binding compiles without implementing business logic.

### WP-003 - Service implementation

- Owner: development phase.
- Contract: frozen version 2.
- Permitted production file: `src/main/bmv/wrike/departments/src/main/java/com/interview/DepartmentService.java`;
  v2 evidence may be updated.
- Dependencies: WP-001 and WP-002.
- Reuse: existing model/DAO plus JDK equality and set facilities.
- Completion: TEST-001 through TEST-020 pass through the production binding
  binding, the placeholder is gone, and ISSUE-001 through ISSUE-007 are fixed.

### WP-004 - Review and integration report

- Owner: development phase after WP-003.
- Permitted files: v2 status/evidence unless a verified in-scope defect requires
  a service correction.
- Work: run the complete suite, inspect duplication/diff, and report ISSUE-008
  through ISSUE-011 without claiming datastore or endpoint guarantees.
- Completion: commands and results are recorded; concurrency and authentication
  limitations are explicit.

No phases may concurrently edit a shared file.
