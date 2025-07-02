package com.efm.efm_backend.dto;

import java.util.List;

public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String projectCode;
    private List<ProjectFileDTO> files;

    // Constructors
    public ProjectDTO() {}

    public ProjectDTO(Long projectId, String projectName, String projectCode, List<ProjectFileDTO> files) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectCode = projectCode;
        this.files = files;
    }

    // Getters and Setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public List<ProjectFileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<ProjectFileDTO> files) {
        this.files = files;
    }
}