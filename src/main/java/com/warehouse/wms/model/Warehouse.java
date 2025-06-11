package com.warehouse.wms.model;

public class Warehouse {
    private int warehouseId;
    private String name;
    private String location;
    private String description;
    
    // Constructors
    public Warehouse() {}
    
    public Warehouse(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }
    
    // Getters and Setters
    public int getWarehouseId() {
        return warehouseId;
    }
    
    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId=" + warehouseId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
