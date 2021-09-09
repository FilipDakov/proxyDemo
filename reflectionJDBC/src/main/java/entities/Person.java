package entities;

public class Person extends BaseEntity {
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName =(String) firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = (String)lastName;
    }

    public Integer getVersion() {
        return version;
    }

}
