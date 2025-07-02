package com.efm.efm_backend.dto;


import com.efm.efm_backend.entity.Employee;

public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;

    // Constructor
    public EmployeeDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    // Static factory for mapping
    public static EmployeeDTO fromEntity(Employee employee) {
        return new EmployeeDTO(employee.getId(), employee.getFirstName(), employee.getLastName());
    }
}