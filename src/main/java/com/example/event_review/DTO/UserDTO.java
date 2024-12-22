package com.example.event_review.DTO;

public class UserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String phoneNumber;

    // Instead of separate roleId and roleName, embed a RolesDTO
    private RolesDTO roles;

    public UserDTO() {}

    // Constructor
    public UserDTO(Long userId, String firstName, String lastName, String gender,
                   String email, String phoneNumber, RolesDTO roles) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    // Getters/Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public RolesDTO getRoles() { return roles; }
    public void setRoles(RolesDTO roles) { this.roles = roles; }
}
