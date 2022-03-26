package edu.byu.cs.tweeter.model.domain.dto;

public class UserDTO {
    private String firstName;
    private String lastName;
    private String userAlias;

    public UserDTO() {}

    public UserDTO(String userAlias, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userAlias = userAlias;
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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }
}
