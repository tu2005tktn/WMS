package com.warehouse.wms.model;

public class Customer {
    private int customerId;
    private String customerName;
    private String email;
    private String address;
    
    // Constructors
    public Customer() {}
    
    public Customer(String customerName, String email, String address) {
        this.customerName = customerName;
        this.email = email;
        this.address = address;
    }
    
    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
