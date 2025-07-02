package com.efm.efm_backend.controller;

import com.efm.efm_backend.dto.ProjectFileDTO;
import com.efm.efm_backend.entity.Document;
import com.efm.efm_backend.entity.Employee;
import com.efm.efm_backend.entity.ProjectFile;
import com.efm.efm_backend.repository.DocumentRepository;
import com.efm.efm_backend.repository.EmployeeRepository;
import com.efm.efm_backend.repository.ProjectFileRepository;
import com.efm.efm_backend.service.AiAnalysisService;
import com.efm.efm_backend.service.DocumentService;
import com.efm.efm_backend.service.ProjectService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.efm.efm_backend.service.AiAnalysisService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private ProjectFileRepository fileRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DocumentService documentService;

    /**
     * Upload a file to a project and branch.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("branchId") Long branchId
    ) {
        try {
            // Validate Employee existence
            Optional<Employee> employeeOpt = employeeRepo.findById(employeeId);
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Employee not found");
            }

            // Delegate file saving to ProjectService
            String result = projectService.uploadFileToProject(projectId, file, branchId, employeeOpt.get());
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * General list endpoint with optional filters.
     */
    @GetMapping
    public ResponseEntity<List<ProjectFileDTO>> listFiles(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String branch
    ) {
        List<ProjectFile> files;
        if (projectId != null && branch != null) {
            files = fileRepo.findByProject_ProjectIdAndBranch_BranchName(projectId, branch);
        } else if (projectId != null) {
            files = fileRepo.findByProject_ProjectId(projectId);
        } else if (employeeId != null) {
            files = fileRepo.findByUploadedBy_Id(employeeId);
        } else if (branch != null) {
            files = fileRepo.findByBranch_BranchName(branch);
        } else {
            files = fileRepo.findAll();
        }
        List<ProjectFileDTO> fileDTOs = files.stream()
                .map(projectService::convertToProjectFileDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fileDTOs);
    }

    /**
     * List all files for a given project.
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> listFilesForProject(@PathVariable Long projectId) {
        List<ProjectFile> files = fileRepo.findByProject_ProjectId(projectId);
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No files found for project " + projectId);
        }
        // Convert to DTOs
        List<ProjectFileDTO> fileDTOs = files.stream()
                .map(projectService::convertToProjectFileDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fileDTOs);
    }

    /**
     * List all files for a given project and branch.
     */
    @GetMapping("/project/{projectId}/branch/{branchName}")
    public ResponseEntity<?> listFilesForProjectBranch(@PathVariable Long projectId, @PathVariable String branchName) {
        List<ProjectFile> files = fileRepo.findByProject_ProjectIdAndBranch_BranchName(projectId, branchName);
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No files found for project " + projectId + " and branch " + branchName);
        }
        // Convert to DTOs
        List<ProjectFileDTO> fileDTOs = files.stream()
                .map(projectService::convertToProjectFileDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fileDTOs);
    }
    /**
     * Download a file by fileId.
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
        Optional<ProjectFile> fileOpt = fileRepo.findById(fileId);
        if (fileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found with id: " + fileId);
        }
        ProjectFile file = fileOpt.get();

        // Assuming ProjectFile has getFileName() and getFileData() (byte[])
        ByteArrayResource resource = new ByteArrayResource(file.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.getFileData().length)
                .body(resource);
    }

    @PostMapping("/analyze/{fileId}")
    public String analyzeFile(
            @PathVariable Long fileId,
            @RequestParam String userQuery) throws Exception {
        return documentService.analyzeDocument(fileId, userQuery);
    }

    // By employee ID (existing)
    @PostMapping("/analyze/employee/{employeeId}")
    public String analyzeEmployee(@PathVariable Long employeeId, @RequestParam String userQuery) throws Exception {
        return documentService.analyzeEmployeeDocuments(employeeId, userQuery);
    }

    // By employee name (first + last)
    @PostMapping("/analyze/employee/by-name")
    public String analyzeEmployeeByName(@RequestParam String employeeName, @RequestParam String userQuery) throws Exception {
        return documentService.analyzeEmployeeDocumentsByName(employeeName, userQuery);
    }

    // General chat (no employee)
    @PostMapping("/chat")
    public String chatWithAi(@RequestParam String userQuery) throws Exception {
        return documentService.chatWithAi(userQuery);
    }
    @GetMapping("/employees/{id}/role-projects")
    public String getEmployeeRoleAndProjects(@PathVariable Long id) {
        return documentService.getEmployeeRoleAndProjects(id);
    }

}