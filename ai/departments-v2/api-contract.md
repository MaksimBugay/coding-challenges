# Department service API contract

## Contract version 2

Owner: `com.interview.DepartmentService`

Construction: `new DepartmentService(DepartmentDao dao)` with a non-null DAO.

## API-001 - Move a department

```java
public boolean moveDepartment(Long departmentId, Long newParentId, Long managerId)
```

All IDs must be non-null and resolve as needed. The target must currently be a
child. The proposed parent must not be the target or its descendant. The trusted
acting manager must directly or hierarchically manage both the target and proposed
parent.

On success, the method changes only the target's `parentId`, supplies that same
object to one DAO `save`, and returns `true`. On a contract rejection it returns
`false`, leaves the target unchanged, and does not save. Missing data and detected
cycles fail closed. DAO runtime exceptions propagate. Logging and same-parent
requests are unspecified.

Given:

- `rootA(id=1,parent=null,manager=10)`
- `engineering(id=2,parent=1,manager=20)`
- `platform(id=3,parent=2,manager=30)`
- `rootB(id=4,parent=null,manager=10)`
- `sales(id=5,parent=4,manager=40)`

the required examples are:

| Invocation | Result and effect |
| --- | --- |
| `moveDepartment(3, 5, 10)` | `true`; platform parent becomes 5 and platform is saved once. This also demonstrates multiple roots. |
| `moveDepartment(3, 5, 20)` | `false`; manager 20 does not manage sales. |
| `moveDepartment(1, 5, 10)` | `false`; rootA cannot become a child. |
| `moveDepartment(3, null, 10)` | `false`; platform cannot become a root. |
| `moveDepartment(2, 3, 10)` | `false`; platform is engineering's descendant. |
| `moveDepartment(3, 3, 30)` | `false`; a department cannot parent itself. |
| `moveDepartment(3, 999, 10)` | `false`; the proposed parent is missing. |

## API-002 - Determine manager authority

```java
protected boolean isUserAllowed(Department department, Long managerId)
```

Returns `true` when the supplied manager ID equals the manager of the supplied
department or an ancestor. Returns `false` for no match, null inputs, missing
ancestry, or detected repeated IDs. It performs DAO reads only and does not mutate
or save.

For the hierarchy above, managers 30, 20, and 10 are allowed for platform;
manager 40 is denied.

## API-003 - Deployment integration contract

An external move request contains `departmentId` and `newParentId`. A protected
caller authenticates the user and derives the `managerId` passed to API-001. The
client does not choose that identity.

The deployed persistence adapter must give API-001 a stable view from its first
read through its save. Contract version 2 does not prescribe a datastore-specific
transaction or add unsupported methods to `DepartmentDao`; therefore the supplied
in-memory service API alone does not guarantee correctness under concurrent
writers.

