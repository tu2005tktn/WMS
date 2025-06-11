package com.warehouse.wms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleCondition {
    private int saleConditionId;
    private String conditionCode;
    private BigDecimal amount;
    private String type; // '%' for percentage, 'fixed' for fixed amount
    private LocalDateTime effectiveDate;
    private LocalDateTime expiredDate;
    private String createdBy;
    private LocalDateTime createdDate;

    // Constructors
    public SaleCondition() {}

    public SaleCondition(String conditionCode, BigDecimal amount, String type, 
                        LocalDateTime effectiveDate, LocalDateTime expiredDate, String createdBy) {
        this.conditionCode = conditionCode;
        this.amount = amount;
        this.type = type;
        this.effectiveDate = effectiveDate;
        this.expiredDate = expiredDate;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getSaleConditionId() {
        return saleConditionId;
    }

    public void setSaleConditionId(int saleConditionId) {
        this.saleConditionId = saleConditionId;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
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

    // Utility methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(effectiveDate) && (expiredDate == null || now.isBefore(expiredDate));
    }

    public boolean isPercentage() {
        return "%".equals(type);
    }

    public boolean isFixedAmount() {
        return "fixed".equals(type);
    }

    @Override
    public String toString() {
        return "SaleCondition{" +
                "saleConditionId=" + saleConditionId +
                ", conditionCode='" + conditionCode + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", effectiveDate=" + effectiveDate +
                ", expiredDate=" + expiredDate +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
