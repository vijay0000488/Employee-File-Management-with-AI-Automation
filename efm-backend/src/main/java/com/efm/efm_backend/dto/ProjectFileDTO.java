package com.efm.efm_backend.dto;

public class ProjectFileDTO {
    private Long fileId;
    private String fileName;
    private String fileType;

    // Constructors
    public ProjectFileDTO() {}

    public ProjectFileDTO(Long fileId, String fileName, String fileType) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    // Getters and Setters
    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}