package bmv.wrike.departments.src.main.java.com.interview;


public interface DepartmentDao {
    Department findById(long id);
    void save(Department department);
}
