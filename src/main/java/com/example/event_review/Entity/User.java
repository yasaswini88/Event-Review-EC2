package com.example.event_review.Entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String password;
    private String phoneNumber;

    // ======== NEW Many-to-Many Relationship ========
    @ManyToMany
    @JoinTable(
        name = "user_roles",                   // Name of the join table
        joinColumns = @JoinColumn(name = "user_id"),      // FK column in "user_roles" for User
        inverseJoinColumns = @JoinColumn(name = "role_id") // FK column in "user_roles" for Roles
    )
    private Set<Roles> roles = new HashSet<>();

    // ======== Getters & Setters ========
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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Set<Roles> getRoles() { return roles; }
    public void setRoles(Set<Roles> roles) { this.roles = roles; }

    @Override
    public String toString() {
        return "User [userId=" + userId +
               ", firstName=" + firstName +
               ", lastName=" + lastName +
               ", gender=" + gender +
               ", email=" + email +
               ", password=" + password +
               ", phoneNumber=" + phoneNumber +
               ", roles=" + roles + "]";
    }
}
