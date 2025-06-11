package com.warehouse.wms.model;

import java.time.LocalDateTime;

public class Role {
    private int roleId;
    private String roleName;
    private String description;
    private LocalDateTime createdDate;

    public Role() {}

    public Role(int roleId, String roleName, String description, LocalDateTime createdDate) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.createdDate = createdDate;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
