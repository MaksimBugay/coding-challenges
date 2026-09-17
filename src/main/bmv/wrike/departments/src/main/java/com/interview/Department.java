package bmv.wrike.departments.src.main.java.com.interview;

public class Department {
    private Long id;
    private Long parentId;
    private Long managerId;
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long ownerId) {
        this.managerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
