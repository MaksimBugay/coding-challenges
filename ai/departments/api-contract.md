# Department service API contract

## Contract version 1

Owner type: `com.interview.DepartmentService`

Construction: `new DepartmentService(DepartmentDao dao)`. A non-null DAO must be supplied.

## API-001 — Move a department

```java
public boolean moveDepartment(Long departmentId, Long newParentId, Long managerId)
```

### Inputs

All three IDs must be non-null. The target and proposed parent must exist. The target must be a child, the proposed parent must differ from the target and must not be below it, and the acting manager must directly or hierarchically manage both departments.

### Output and side effects

Returns `true` only after setting the resolved target's `parentId` to `newParentId` and passing that same target object to exactly one DAO `save` call. Returns `false` for every rule rejection, null ID, missing entity, missing traversed ancestor, or detected hierarchy cycle. A `false` result causes no target mutation and no save.

DAO runtime exceptions propagate. Logging and behavior for requesting the already-current parent are unspecified.

### Examples

Given `root(id=1,parent=null,manager=10)`, `engineering(id=2,parent=1,manager=20)`, `platform(id=3,parent=2,manager=30)`, and `sales(id=4,parent=1,manager=40)`:

- `moveDepartment(3, 4, 10)` returns `true`, changes platform's parent to 4, and saves platform once because manager 10 manages both branches through root.
- `moveDepartment(3, 4, 20)` returns `false` without a save because manager 20 does not manage sales.
- `moveDepartment(1, 4, 10)` returns `false` because root cannot become a child.
- `moveDepartment(3, null, 10)` returns `false` because a child cannot become root.
- `moveDepartment(2, 3, 10)` returns `false` because platform is a descendant of engineering.
- `moveDepartment(3, 999, 10)` returns `false` because the proposed parent is missing.

## API-002 — Determine manager authority

```java
protected boolean isUserAllowed(Department department, Long managerId)
```

This operation is accessible to package-local contract tests and subclasses but is not widened to public. It returns `true` if the supplied non-null manager ID equals the manager ID of the supplied department or any ancestor. It returns `false` for null input, no match, a missing ancestor, a null traversed department ID, or a repeated ID.

Using the example hierarchy above:

- `isUserAllowed(platform, 30)`, `isUserAllowed(platform, 20)`, and `isUserAllowed(platform, 10)` return `true`.
- `isUserAllowed(platform, 40)`, `isUserAllowed(platform, null)`, and `isUserAllowed(null, 10)` return `false`.

The operation performs DAO reads only and does not mutate or save a department.
