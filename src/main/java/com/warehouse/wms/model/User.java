package com.warehouse.wms.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private int purchaseStaffId;
    private String username;
    private String passwordHash;
    private String status;
    private LocalDateTime createdDate;
    
    // Staff information
    private String staffName;
    private String email;
    private String address;
    
    // Constructors
    public User() {}
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = "ACTIVE";
    }
    
    public User(int purchaseStaffId, String username, String passwordHash) {
        this.purchaseStaffId = purchaseStaffId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getPurchaseStaffId() {
        return purchaseStaffId;
    }
    
    public void setPurchaseStaffId(int purchaseStaffId) {
        this.purchaseStaffId = purchaseStaffId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", staffName='" + staffName + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
