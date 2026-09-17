# Department hierarchy traceability

Contract version: 2

| Requirement | Acceptance criteria | Boundary | Planned verification | Work package |
| --- | --- | --- | --- | --- |
| REQ-001 | AC-001-01..03 | IF-001/IF-002, API-002 | TEST-001 direct manager; TEST-002 parent/root manager; TEST-003 unrelated manager | WP-002, WP-003 |
| REQ-002 | AC-002-01 | IF-001/IF-002, API-001 | TEST-004 unauthorized target; TEST-005 unauthorized proposed parent; assert no mutation/save | WP-002, WP-003 |
| REQ-003 | AC-003-01..03 | IF-001, API-001 | TEST-006 root-to-child; TEST-007 child-to-root; assert no mutation/save | WP-002, WP-003 |
| REQ-004 | AC-004-01 | IF-001, API-001 | TEST-008 self-parent rejection | WP-002, WP-003 |
| REQ-004 | AC-004-02..03 | IF-001/IF-002, API-001 | TEST-009 descendant rejection with no mutation/save | WP-002, WP-003 |
| REQ-004 | AC-004-04 | IF-001/IF-002, API-001 | TEST-010 valid move across independently rooted branches sharing an ancestor-level manager ID | WP-002, WP-003 |
| REQ-005 | AC-005-01 | IF-001/IF-002, API-001 | TEST-011 valid move result, fields, same-object save, exactly once | WP-002, WP-003 |
| REQ-006 | AC-006-01 | IF-001/IF-002, API-001/API-002 | TEST-007 null new-parent ID; TEST-012 null target ID; TEST-013 null authorization inputs; TEST-018 null move-manager ID | WP-002, WP-003 |
| REQ-006 | AC-006-02 | IF-001/IF-002, API-001 | TEST-014 missing target; TEST-015 missing proposed parent | WP-002, WP-003 |
| REQ-006 | AC-006-03 | IF-001/IF-002, API-001/API-002 | TEST-016/TEST-017 authorization rejects missing/cyclic ancestry; TEST-019/TEST-020 move rejects a proposed parent with missing/cyclic ancestry | WP-002, WP-003 |
| REQ-007 | AC-007-01..02 | IF-002, API-003 | REVIEW-001 confirm and report absent atomicity/version contract; document required adapter guarantee | WP-004 |
| REQ-008 | AC-008-01..02 | IF-003, API-003 | REVIEW-002 verify DTO excludes manager ID and integration guidance derives identity from authentication | WP-004 |
| REQ-009 | AC-009-01 | IF-001, API-001/API-002 | TEST-001..020 and placeholder search | WP-003, WP-004 |
| REQ-009 | AC-009-02 | IF-002/IF-003, API-003 | REVIEW-001, REVIEW-002, final issue disposition | WP-004 |
| REQ-009 | AC-009-03 | IF-001/IF-002 | BUILD-001 root `mvn -DskipTests compile` | WP-001 |

Expected test results come from `api-contract.md`. Mock responses must be declared
by scenario ID independently of the production algorithm. REVIEW-001 and
REVIEW-002 are non-test verification because the repository contains neither a
persistence implementation nor an endpoint.

## Implemented contract suite

TEST-001 through TEST-020 are implemented once in
`src/test/com/interview/DepartmentServiceContract.java`.
`MockDepartmentServiceContract` binds the shared suite to independently declared,
scenario-ID keyed observations and is excluded from ordinary `*Test` discovery.
`DepartmentServiceContractTest` binds the same inputs and assertions to fresh
department graphs, the real `DepartmentService`, and a recording in-memory DAO.

| Test ID | Shared test method |
| --- | --- |
| TEST-001 | `test001AllowsTheDirectManager` |
| TEST-002 | `test002AllowsManagersHigherInTheAncestry` |
| TEST-003 | `test003DeniesAnUnrelatedManager` |
| TEST-004 | `test004RejectsAMoveWhenTheTargetIsUnauthorized` |
| TEST-005 | `test005RejectsAMoveWhenTheNewParentIsUnauthorized` |
| TEST-006 | `test006RejectsMovingARootUnderAnotherDepartment` |
| TEST-007 | `test007RejectsMovingAChildToTheRoot` |
| TEST-008 | `test008RejectsSelfParenting` |
| TEST-009 | `test009RejectsMovingBelowADescendant` |
| TEST-010 | `test010SupportsAValidMoveAcrossSeparateRoots` |
| TEST-011 | `test011ChangesOnlyTheParentAndSavesTheSameTargetOnce` |
| TEST-012 | `test012RejectsANullDepartmentId` |
| TEST-013 | `test013RejectsNullAuthorizationInputs` |
| TEST-014 | `test014RejectsAMissingTarget` |
| TEST-015 | `test015RejectsAMissingNewParent` |
| TEST-016 | `test016RejectsAMissingAncestor` |
| TEST-017 | `test017TerminatesAndRejectsCyclicAncestry` |
| TEST-018 | `test018RejectsANullManagerForAMove` |
| TEST-019 | `test019RejectsANewParentWithAMissingAncestor` |
| TEST-020 | `test020RejectsANewParentWithCyclicAncestry` |

TEST-013 includes independently keyed sub-scenarios for a null department and a
null manager. Every observation checks the result, target state, save count,
saved-object identity, and that every non-target department remains unchanged.

## Testing-phase verification

- Mock command: `mvn -Dtest=MockDepartmentServiceContract test`.
- Testing-phase result after review repair: exit 0; 18 tests, 0 failures, 0
  errors, 0 skipped. Development later added TEST-019 and TEST-020 for complete
  API-001 malformed-new-parent coverage; the current mock result is 20 passing.
- Report: `target/surefire-reports/com.interview.MockDepartmentServiceContract.txt`.
- REVIEW-001: `DepartmentDao` still exposes only independent lookup and void save;
  concurrent atomicity remains an explicit production-adapter obligation.
- REVIEW-002: no endpoint or DTO exists in the repository; API-003 remains an
  integration contract requiring authenticated identity outside client input.

The mock result verifies test mechanics only.

## Production implementation and acceptance

| Requirement | Production implementation | Passing verification |
| --- | --- | --- |
| REQ-001 | `DepartmentService.isUserAllowed` and the shared `scanHierarchy` ancestry walk | TEST-001..003, TEST-013, TEST-016..017 |
| REQ-002 | `moveDepartment` target scan and combined proposed-parent scan | TEST-004..005 |
| REQ-003 | null-ID guard and root-target rejection before mutation | TEST-006..007 |
| REQ-004 | visited-ID validation and forbidden target-ID detection in `scanHierarchy` | TEST-008..010, TEST-020 |
| REQ-005 | parent mutation followed by one `dao.save` after all validation | TEST-010..011 |
| REQ-006 | request/entity guards plus malformed ancestry rejection | TEST-007, TEST-012..020 |
| REQ-007 | DAO limitation retained and documented; no unsupported atomicity claim | REVIEW-001 |
| REQ-008 | authenticated caller requirement retained in API-003 | REVIEW-002 |
| REQ-009 | placeholder and duplicate manual implementation removed; one production hierarchy scan remains | focused/full Maven acceptance and final review |

- Mock contract: `mvn -Dtest=MockDepartmentServiceContract test` - exit 0; 20
  tests, 0 failures, 0 errors, 0 skipped.
- Production contract: `mvn -Dtest=DepartmentServiceContractTest test` - exit 0;
  20 tests, 0 failures, 0 errors, 0 skipped.
- Complete suite: `mvn test` - exit 0; 97 tests across 13 discovered suites, 0
  failures, 0 errors, 0 skipped. `SlowProcessCollectorTest` completed its
  20-million-record workload.
- Reports: `target/surefire-reports/`.
