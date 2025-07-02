package com.efm.efm_backend.service;

import com.efm.efm_backend.entity.Document;
import com.efm.efm_backend.entity.Employee;
import com.efm.efm_backend.repository.DocumentRepository;
import com.efm.efm_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    private final DocumentRepository documentRepository;

    public Employee saveEmployee(Employee emp) {
        Optional<Employee> existing = employeeRepo.findByEmail(emp.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Employee with email already exists");
        }
        return employeeRepo.save(emp);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepo.findById(id);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepo.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepo.deleteById(id);
    }

    public Employee updateEmployee(Long id, Employee updatedEmp) {
        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existing.setFirstName(updatedEmp.getFirstName());
        existing.setLastName(updatedEmp.getLastName());
        existing.setEmail(updatedEmp.getEmail());
        existing.setRole(updatedEmp.getRole()); // <-- Add this line

        return employeeRepo.save(existing);
    }
    public EmployeeService(DocumentRepository documentRepository, EmployeeRepository employeeRepository) {
        this.documentRepository = documentRepository;
        this.employeeRepo = employeeRepository;
    }

    public List<Employee> findEmployeesBySkill(String skill) {
        List<Document> skillsDocs = documentRepository.findAllSkillDocuments();
        List<Employee> matchingEmployees = new ArrayList<>();
        String skillLower = skill.toLowerCase();

        for (Document doc : skillsDocs) {
            String skillsContent = new String(doc.getData(), StandardCharsets.UTF_8).toLowerCase();
            if (skillsContent.contains(skillLower)) {
                Employee emp = doc.getEmployee();
                if (emp != null && skillsContent.contains(skillLower)) {
                    matchingEmployees.add(emp);
                }
            }
        }
        return matchingEmployees;
    }
}
