package com.interview;

import bmv.wrike.departments.src.main.java.com.interview.Department;
import bmv.wrike.departments.src.main.java.com.interview.DepartmentDao;
import bmv.wrike.departments.src.main.java.com.interview.DepartmentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DepartmentServiceContractTest extends DepartmentServiceContract {

  @Override
  protected Operation operationFor(String scenarioId) {
    return DepartmentServiceContractTest::invokeProduction;
  }

  private static Observation invokeProduction(Input input) {
    Map<Long, Department> departments = new HashMap<>();
    input.hierarchy().forEach((id, spec) -> departments.put(id, toDepartment(spec)));

    RecordingDepartmentDao dao = new RecordingDepartmentDao(departments);
    AccessibleDepartmentService service = new AccessibleDepartmentService(dao);
    Department target = departments.get(input.departmentId());

    boolean result = switch (input.action()) {
      case AUTHORIZE -> service.isAllowed(target, input.managerId());
      case MOVE -> service.moveDepartment(
          input.departmentId(), input.newParentId(), input.managerId());
    };

    return new Observation(
        result,
        toState(target),
        dao.saved.size(),
        dao.saved.size() == 1 && dao.saved.getFirst() == target,
        nonTargetsAreUnchanged(input, departments));
  }

  private static boolean nonTargetsAreUnchanged(
      Input input, Map<Long, Department> departments) {
    return input.hierarchy().entrySet().stream()
        .filter(entry -> !entry.getKey().equals(input.departmentId()))
        .allMatch(entry -> toState(departments.get(entry.getKey()))
            .equals(toState(entry.getValue())));
  }

  private static Department toDepartment(DepartmentSpec spec) {
    Department department = new Department();
    department.setId(spec.id());
    department.setParentId(spec.parentId());
    department.setManagerId(spec.managerId());
    department.setTitle(spec.title());
    return department;
  }

  private static DepartmentState toState(Department department) {
    if (department == null) {
      return null;
    }
    return new DepartmentState(
        department.getId(),
        department.getParentId(),
        department.getManagerId(),
        department.getTitle());
  }

  private static DepartmentState toState(DepartmentSpec department) {
    return new DepartmentState(
        department.id(),
        department.parentId(),
        department.managerId(),
        department.title());
  }

  private static final class AccessibleDepartmentService extends DepartmentService {
    private AccessibleDepartmentService(DepartmentDao dao) {
      super(dao);
    }

    private boolean isAllowed(Department department, Long managerId) {
      return isUserAllowed(department, managerId);
    }
  }

  private static final class RecordingDepartmentDao implements DepartmentDao {
    private final Map<Long, Department> departments;
    private final List<Department> saved = new ArrayList<>();

    private RecordingDepartmentDao(Map<Long, Department> departments) {
      this.departments = departments;
    }

    @Override
    public Department findById(long id) {
      return departments.get(id);
    }

    @Override
    public void save(Department department) {
      saved.add(department);
    }
  }
}
