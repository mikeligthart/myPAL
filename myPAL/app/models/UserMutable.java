package models;

import play.data.validation.Constraints;

/**
 * Created by ligthartmeu on 17-6-2015.
 */
public class UserMutable{

    @Constraints.Required(message = "This is field required")
    private String firstName;

    @Constraints.Required(message = "This is field required")
    private String lastName;

    @Constraints.Required(message = "This is field required")
    private String userType;

    public UserMutable(String firstName, String lastName, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    public UserMutable(){

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
