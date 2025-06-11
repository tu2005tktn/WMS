package com.warehouse.wms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseOrderDetail {
    private int poDetailId;
    private int poId;
    private int productId;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime deliveryDate;
    private LocalDateTime actualDelivery;
    private String status;
    
    // Product information (for display)
    private String productCode;
    private String productName;
    private String productCategory;
    private BigDecimal productCost;
    
    // Calculated fields
    private BigDecimal lineTotal;
    
    // Constructors
    public PurchaseOrderDetail() {}
    
    public PurchaseOrderDetail(int poId, int productId, int quantity, BigDecimal price, LocalDateTime deliveryDate, String status) {
        this.poId = poId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }
    
    // Getters and Setters
    public int getPoDetailId() {
        return poDetailId;
    }
    
    public void setPoDetailId(int poDetailId) {
        this.poDetailId = poDetailId;
    }
    
    public int getPoId() {
        return poId;
    }
    
    public void setPoId(int poId) {
        this.poId = poId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public LocalDateTime getActualDelivery() {
        return actualDelivery;
    }
    
    public void setActualDelivery(LocalDateTime actualDelivery) {
        this.actualDelivery = actualDelivery;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public BigDecimal getProductCost() {
        return productCost;
    }
    
    public void setProductCost(BigDecimal productCost) {
        this.productCost = productCost;
    }
    
    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    
    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
    
    // Helper methods
    public void calculateLineTotal() {
        if (price != null && quantity > 0) {
            lineTotal = price.multiply(BigDecimal.valueOf(quantity));
        } else {
            lineTotal = BigDecimal.ZERO;
        }
    }
}
