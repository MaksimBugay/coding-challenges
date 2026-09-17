package bmv.wrike.departments.src.main.java.com.interview;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 1. A department can only be changed by its direct or hierarchical manager.
 * 2. Root departments cannot be changed to child ones, child departments cannot be changed into root ones.
 * 3. A new parent for a changing department must be owned directly or hierarchically by the user who makes changes.
 */
public class DepartmentService {
    
    private static final Logger log = Logger.getLogger(DepartmentService.class.getName());

    private final DepartmentDao dao;

    public DepartmentService(DepartmentDao dao) {
        this.dao = dao;
    }
  
    public boolean moveDepartment(Long departmentId, Long newParentId, Long managerId) {
        if (departmentId == null || newParentId == null || managerId == null) {
            return false;
        }

        Department department = dao.findById(departmentId);
        if (department == null) {
            return false;
        }

        if (!isUserAllowed(department, managerId)) {
            log.warning("Req1: A department can only be changed by its direct or hierarchical manager");
            return false;
        }
        if (department.getParentId() == null) {
            log.warning("Req2: Root departments cannot be changed to child ones");
            return false;
        }

        Department newParent = dao.findById(newParentId);
        HierarchyScan newParentHierarchy = scanHierarchy(newParent, managerId, departmentId);
        if (!newParentHierarchy.valid()
                || !newParentHierarchy.managerFound()
                || newParentHierarchy.forbiddenDepartmentFound()) {
            log.warning("Req3: A new parent for a changing department must be owned directly or hierarchically by the user who makes changes");
            return false;
        }

        department.setParentId(newParentId);
        dao.save(department);
        return true;
    }

    protected boolean isUserAllowed(Department department, Long managerId) {
        if (department == null || managerId == null) {
            return false;
        }

        HierarchyScan hierarchy = scanHierarchy(department, managerId, null);
        return hierarchy.valid() && hierarchy.managerFound();
    }

    private HierarchyScan scanHierarchy(
            Department department, Long managerId, Long forbiddenDepartmentId) {
        if (department == null) {
            return HierarchyScan.INVALID;
        }

        Set<Long> visitedDepartmentIds = new HashSet<>();
        boolean managerFound = false;
        boolean forbiddenDepartmentFound = false;
        Department current = department;

        while (current != null) {
            Long currentId = current.getId();
            if (currentId == null || !visitedDepartmentIds.add(currentId)) {
                return HierarchyScan.INVALID;
            }

            managerFound |= Objects.equals(current.getManagerId(), managerId);
            forbiddenDepartmentFound |= Objects.equals(currentId, forbiddenDepartmentId);

            Long parentId = current.getParentId();
            if (parentId == null) {
                return new HierarchyScan(true, managerFound, forbiddenDepartmentFound);
            }

            current = dao.findById(parentId);
        }

        return HierarchyScan.INVALID;
    }

    private record HierarchyScan(
            boolean valid,
            boolean managerFound,
            boolean forbiddenDepartmentFound) {
        private static final HierarchyScan INVALID = new HierarchyScan(false, false, false);
    }
}
