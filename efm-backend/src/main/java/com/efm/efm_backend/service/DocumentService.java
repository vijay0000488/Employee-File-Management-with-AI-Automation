package com.efm.efm_backend.service;

import com.efm.efm_backend.entity.Document;
import com.efm.efm_backend.entity.Employee;
import com.efm.efm_backend.entity.Project;
import com.efm.efm_backend.repository.DocumentRepository;
import com.efm.efm_backend.repository.EmployeeRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AiAnalysisService aiAnalysisService;

    public Document uploadDocument(Long employeeId, MultipartFile file, String documentType) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setData(file.getBytes());
        document.setDocumentType(documentType);
        document.setEmployee(employee);

        return documentRepository.save(document);
    }

    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public List<Document> getDocumentsByEmployee(Long employeeId) {
        return documentRepository.findByEmployeeId(employeeId);
    }

    public Document updateDocument(Long documentId, MultipartFile file, String documentType) throws IOException {
        Document document = getDocument(documentId);
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setData(file.getBytes());
        document.setDocumentType(documentType);
        return documentRepository.save(document);
    }

    public void deleteDocument(Long documentId) {
        documentRepository.deleteById(documentId);
    }

    public String analyzeDocument(Long documentId, String userQuery) throws Exception {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Extract text using Apache Tika
        Tika tika = new Tika();
        String content = tika.parseToString(new ByteArrayInputStream(doc.getData()));

        // Call AI
        return aiAnalysisService.analyzeDocument(content, userQuery);
    }

    public String analyzeEmployeeDocuments(Long employeeId, String userQuery) throws Exception {
        List<Document> docs = documentRepository.findByEmployeeId(employeeId);
        if (docs.isEmpty()) {
            throw new RuntimeException("No documents found for employee id " + employeeId);
        }

        Tika tika = new Tika();
        StringBuilder combinedText = new StringBuilder();

        for (Document doc : docs) {
            try {
                String text = tika.parseToString(new java.io.ByteArrayInputStream(doc.getData()));
                combinedText.append(text).append("\n\n");
            } catch (Exception e) {
                // Optionally log and continue with other docs
            }
        }

        if (combinedText.length() == 0) {
            throw new RuntimeException("No extractable text found for employee id " + employeeId);
        }

        return aiAnalysisService.analyzeDocument(combinedText.toString(), userQuery);
    }

    // New: Analyze by first and last name
    public String analyzeEmployeeDocumentsByName(String employeeName, String userQuery) throws Exception {
        // Split the employeeName into first and last name (assumes "First Last")
        String[] nameParts = employeeName.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Optional<Employee> employeeOpt = employeeRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        Employee employee = employeeOpt.orElseThrow(() -> new RuntimeException("Employee not found with first name: " + firstName + " and last name: " + lastName));

        // Use your existing method to analyze by ID
        return analyzeEmployeeDocuments(employee.getId(), userQuery);
    }

    // General chat (no employee context)
    public String chatWithAi(String userQuery) throws Exception {
        // Replace with your actual AI service logic
        return aiAnalysisService.analyzeDocument("", userQuery);
    }

    public String getEmployeeRoleAndProjects(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return "Employee not found with ID: " + employeeId;
        }
        Employee employee = employeeOpt.get();

        // Get role
        String role = (employee.getRole() != null) ? employee.getRole().name() : "No role assigned";

        // Get project names
        Set<Project> projects = employee.getProjects();
        String projectsStr = projects.isEmpty()
                ? "No projects assigned"
                : projects.stream().map(Project::getProjectName).collect(Collectors.joining(", "));
    //).collect(Collectors.joining(", "));

    return "Role: " + role + "\nProjects: " + projectsStr;
}

}