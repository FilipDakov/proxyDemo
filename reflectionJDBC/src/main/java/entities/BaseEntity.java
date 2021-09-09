package entities;

public class BaseEntity {
    protected Long id;
    protected Integer version;

    public Integer getVersion() {
        return version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = new Long(id.toString());
    }

    public void setVersion(Object version) {
        this.version = Integer.valueOf(version.toString());
    }
}
