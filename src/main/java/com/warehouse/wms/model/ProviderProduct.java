package com.warehouse.wms.model;

import java.math.BigDecimal;

public class ProviderProduct {
    private int providerId;
    private int productId;
    private Integer deliveryDuration; // in days
    private BigDecimal estimatedPrice;
    private String policies;
    
    // For display purposes - not stored in DB
    private String providerName;
    private String productName;
    private String productCode;
    private String productCategory;
    
    public ProviderProduct() {}
    
    public ProviderProduct(int providerId, int productId) {
        this.providerId = providerId;
        this.productId = productId;
    }
    
    public ProviderProduct(int providerId, int productId, Integer deliveryDuration, 
                          BigDecimal estimatedPrice, String policies) {
        this.providerId = providerId;
        this.productId = productId;
        this.deliveryDuration = deliveryDuration;
        this.estimatedPrice = estimatedPrice;
        this.policies = policies;
    }
    
    // Getters and Setters
    public int getProviderId() {
        return providerId;
    }
    
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public Integer getDeliveryDuration() {
        return deliveryDuration;
    }
    
    public void setDeliveryDuration(Integer deliveryDuration) {
        this.deliveryDuration = deliveryDuration;
    }
    
    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }
    
    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
    
    public String getPolicies() {
        return policies;
    }
    
    public void setPolicies(String policies) {
        this.policies = policies;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    @Override
    public String toString() {
        return "ProviderProduct{" +
                "providerId=" + providerId +
                ", productId=" + productId +
                ", deliveryDuration=" + deliveryDuration +
                ", estimatedPrice=" + estimatedPrice +
                ", policies='" + policies + '\'' +
                ", providerName='" + providerName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
