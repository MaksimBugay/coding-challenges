# Department hierarchy architecture

## Status

- Phase: solution design blocked pending Q-001
- Contract version: 1
- Design blocker: scope of endpoint/security/DTO and concurrent-update notes in `task-img.png`
- Production behavior remains intentionally incomplete until development

## Reuse findings

Repository searches found no other department hierarchy implementation, caller, test, or reusable ancestry traversal. The supplied `Department`, `DepartmentDao`, and `DepartmentService` own the same responsibility and are retained. JDK equality and a `HashSet<Long>` are sufficient; no new production abstraction or dependency is justified.

## IF-001 — Department service

Traces to REQ-001 through REQ-008.

```java
package com.interview;

public class DepartmentService {
    public DepartmentService(DepartmentDao dao);
    public boolean moveDepartment(Long departmentId, Long newParentId, Long managerId);
    protected boolean isUserAllowed(Department department, Long managerId);
}
```

The constructor-injected DAO is the only collaborator and the existing concrete service remains the production boundary. Construction with a null DAO is outside the contract; production and test bindings must supply one.

## IF-002 — Persistence seam

```java
package com.interview;

public interface DepartmentDao {
    Department findById(long id);
    void save(Department department);
}
```

`findById` supplies departments for lookup and traversal and returns null for an unknown ID in contract fixtures. `save` receives the moved target. Persistence, transactions, and concurrent consistency are outside this exercise.

## Contract version 1 design

Authorization walks from the supplied department toward the root. At each node it compares manager IDs by value and records the department ID before following `parentId`. Direct or ancestor equality succeeds. Null input, missing data, a null node ID, or a repeated ID fails safely.

Moving first rejects null request IDs, then resolves the target and proposed parent. It verifies target authorization, rejects a root target, verifies proposed-parent authorization, and confirms that the proposed parent's ancestry neither reaches the target nor becomes malformed. Only then does it mutate the target and save it once.

The implementation should reuse one narrow private ancestry-walk mechanism or equivalent shared guard so missing-node and cycle handling are not copied inconsistently between authorization and descendant detection. Expected cost is `O(h)` DAO lookups and `O(h)` auxiliary storage, where `h` is the combined traversed hierarchy height. The service owns no mutable cross-call state, but thread safety and atomic snapshots depend on the DAO and are unspecified.

Logging is diagnostic and has no contractual wording, count, or severity. DAO-thrown runtime exceptions propagate. On every contract-level `false` result, the target is not mutated and `save` is not called.

## Independent contract-test seams

The testing phase should create shared black-box fixtures under `src/test/com/interview/`:

- `DepartmentAuthorizationContract.java`
- `DepartmentMoveContract.java`
- `MockDepartmentContracts.java`
- `DepartmentServiceContractTest.java`

Each abstract contract exposes a small test-scoped functional operation plus `operationFor(String scenarioId)`. The mock binding uses scenario-ID keyed literal responses declared separately from assertion fixtures. The production binding builds `Department` graphs and a recording in-memory `DepartmentDao`, then invokes `DepartmentService`. Because tests use package `com.interview`, the authorization contract can access the existing protected method without widening the production API. The same scenario inputs and assertions must run against both bindings; expected values must come from [api-contract.md](api-contract.md), never from production code.

No new test dependency is needed. Preserve the root Maven Mockito agent configuration even though a small in-memory DAO is sufficient.

Planned commands:

- Declaration compile: `mvn -DskipTests compile`
- Mock contracts: `mvn -Dtest=MockDepartmentContracts test`
- Production contracts: `mvn -Dtest=DepartmentServiceContractTest test`
- Final suite: `mvn test`, including `SlowProcessCollectorTest`

## Work packages

### WP-001 — Repair and expose compilable declarations

- Owner: solution-design phase.
- Contract: version 1.
- Permitted files: the three supplied Java declarations and the five `ai/departments/` documents.
- Work: normalize all declarations to `com.interview`; replace unresolved field injection with the public DAO constructor; retain the supplied incomplete business behavior for later phases.
- Completion: declarations match IF-001/IF-002 and the root Maven compile succeeds.

### WP-002 — Build independent black-box tests

- Owner: testing phase.
- Contract: frozen version 1.
- Permitted files: the four planned test files and handoff status/evidence files.
- Dependency: WP-001 complete.
- Work: implement TEST-001 through TEST-016 from [traceability.md](traceability.md), first against the independent mock binding, then compile the production binding.
- Completion: every shared assertion passes against the mock; production tests compile without changing production behavior.

### WP-003 — Implement authorization and safe moves

- Owner: development phase.
- Contract: frozen version 1.
- Permitted production file: `src/main/bmv/wrike/departments/src/main/java/com/interview/DepartmentService.java`; handoff status/evidence may also be updated.
- Dependencies: WP-001 and WP-002 complete.
- Reuse: supplied model/DAO, JDK value equality, and `HashSet` cycle detection.
- Work: replace the authorization placeholder and repair `moveDepartment` for REQ-001 through REQ-007 without changing tester assertions.
- Complexity: `O(h)` DAO lookups/time and `O(h)` space per call.
- Completion: focused production contracts pass and no required review finding remains unfixed.

### WP-004 — Final review and verification

- Owner: development phase after WP-003.
- Permitted files: handoff status/evidence only unless a verified defect requires an in-scope correction.
- Work: run the complete suite; search again for duplicate hierarchy logic; inspect the diff for accidental changes; report ISSUE-009 and ISSUE-010 as scoped findings.
- Completion: actual results and any resource limitation are recorded.

No phases may concurrently edit a shared file.
