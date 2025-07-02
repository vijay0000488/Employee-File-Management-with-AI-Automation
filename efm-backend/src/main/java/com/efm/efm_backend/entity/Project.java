package com.efm.efm_backend.entity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

@Getter
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_code", unique = true)
    private String projectCode;
    @ManyToMany
    @JoinTable(
            name = "project_employee",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectFile> files = new ArrayList<>();

    // === Getters and Setters ===

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public void setFiles(List<ProjectFile> files) {
        this.files = files;
    }
}


