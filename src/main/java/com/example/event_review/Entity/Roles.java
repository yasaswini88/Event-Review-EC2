package com.example.event_review.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long roleId;
    private String roleName;


    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoles(String roleName) {
        this.roleName = roleName;
    }
    public Roles(Long roleId, String roleName) {
        super();
        this.roleId = roleId;
        this.roleName = roleName;
    }
    public Roles() {
        super();

    }
    @Override
    public String toString() {
        return "Roles [roleId=" + roleId + ", roleName=" + roleName + ", getRoleId()=" + getRoleId()
                + ", getRoleName()=" + getRoleName() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
                + ", toString()=" + super.toString() + "]";
    }


}
