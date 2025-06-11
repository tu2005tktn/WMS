package com.warehouse.wms.model;

public class UserImage {
    private byte[] data;
    private String contentType;
    private String fileName;

    public UserImage(byte[] data, String contentType, String fileName) {
        this.data = data;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }
}
