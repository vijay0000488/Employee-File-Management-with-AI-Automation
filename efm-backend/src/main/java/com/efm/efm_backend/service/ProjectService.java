package com.efm.efm_backend.service;

import com.efm.efm_backend.dto.ProjectDTO;
import com.efm.efm_backend.dto.ProjectFileDTO;
import com.efm.efm_backend.entity.ProjectFile;
import com.efm.efm_backend.entity.Employee;
import com.efm.efm_backend.entity.Project;
import com.efm.efm_backend.entity.Branch;
import com.efm.efm_backend.repository.ProjectRepository;
import com.efm.efm_backend.repository.ProjectFileRepository;
import com.efm.efm_backend.repository.BranchRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private ProjectFileRepository fileRepo;

    @Autowired
    private BranchRepository branchRepo;
    private EntityManager entityManager;
    public Project createProject(Project project) {
        String code = project.getProjectCode();

        if (code == null || code.trim().isEmpty()) {
            throw new RuntimeException("Project code cannot be null or empty");
        }

        if (projectRepo.existsByProjectCode(code)) {
            throw new RuntimeException("Project with this code already exists");
        }

        return projectRepo.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepo.findById(id);
    }

    public Project updateProject(Long id, Project updatedProject) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        project.setProjectName(updatedProject.getProjectName());
        project.setProjectCode(updatedProject.getProjectCode());
        return projectRepo.save(project);
    }

    public void deleteProject(Long id) {
        projectRepo.deleteById(id);
    }
    @Transactional
    public String uploadFileToProject(Long projectId, MultipartFile file, Long branchId, Employee employee) throws IOException {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        String fileName = file.getOriginalFilename();

        ProjectFile pf = new ProjectFile();
        pf.setFileName(fileName);
        pf.setFileType(file.getContentType());
        pf.setBranch(branch);
        pf.setProject(project);
        pf.setUploadedBy(employee);
        // Create Blob from MultipartFile
        // Add this Blob creation code:
        pf.setFileData(file.getBytes());


        fileRepo.save(pf);

        return "File uploaded successfully.";
    }

    // In ProjectService.java
    public ProjectFileDTO convertToProjectFileDTO(ProjectFile file) {
        return new ProjectFileDTO(file.getId(), file.getFileName(), file.getFileType());
    }

    public ProjectDTO convertToProjectDTO(Project project) {
        List<ProjectFileDTO> fileDTOs = project.getFiles()
                .stream()
                .map(this::convertToProjectFileDTO)
                .collect(Collectors.toList());
        return new ProjectDTO(
                project.getProjectId(),
                project.getProjectName(),
                project.getProjectCode(),
                fileDTOs
        );
    }
}