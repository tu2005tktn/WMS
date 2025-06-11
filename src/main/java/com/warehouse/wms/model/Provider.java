package com.warehouse.wms.model;

public class Provider {
    private int providerId;
    private String providerName;
    private String email;
    private String address;
    
    // Constructors
    public Provider() {}
    
    public Provider(String providerName, String email, String address) {
        this.providerName = providerName;
        this.email = email;
        this.address = address;
    }
    
    // Getters and Setters
    public int getProviderId() {
        return providerId;
    }
    
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
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
        return "Provider{" +
                "providerId=" + providerId +
                ", providerName='" + providerName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
