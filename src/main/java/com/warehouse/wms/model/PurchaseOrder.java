package com.warehouse.wms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrder {
    private int poId;
    private int providerId;
    private LocalDateTime date;
    private LocalDateTime deliveryDate;
    private String status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String notes;
    
    // Provider information (for display)
    private String providerName;
    private String providerEmail;
    private String providerAddress;
    
    // Purchase order details
    private List<PurchaseOrderDetail> details;
    
    // Calculated fields
    private BigDecimal totalAmount;
    private int totalQuantity;
    
    // Constructors
    public PurchaseOrder() {}
    
    public PurchaseOrder(int providerId, LocalDateTime deliveryDate, String status, String createdBy, String notes) {
        this.providerId = providerId;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.createdBy = createdBy;
        this.notes = notes;
        this.date = LocalDateTime.now();
        this.createdDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getPoId() {
        return poId;
    }
    
    public void setPoId(int poId) {
        this.poId = poId;
    }
    
    public int getProviderId() {
        return providerId;
    }
    
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public String getProviderEmail() {
        return providerEmail;
    }
    
    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }
    
    public String getProviderAddress() {
        return providerAddress;
    }
    
    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }
    
    public List<PurchaseOrderDetail> getDetails() {
        return details;
    }
    
    public void setDetails(List<PurchaseOrderDetail> details) {
        this.details = details;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    // Helper methods
    public void calculateTotals() {
        if (details != null && !details.isEmpty()) {
            totalAmount = BigDecimal.ZERO;
            totalQuantity = 0;
            
            for (PurchaseOrderDetail detail : details) {
                if (detail.getPrice() != null) {
                    BigDecimal lineTotal = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
                    totalAmount = totalAmount.add(lineTotal);
                }
                totalQuantity += detail.getQuantity();
            }
        } else {
            totalAmount = BigDecimal.ZERO;
            totalQuantity = 0;
        }
    }
}
