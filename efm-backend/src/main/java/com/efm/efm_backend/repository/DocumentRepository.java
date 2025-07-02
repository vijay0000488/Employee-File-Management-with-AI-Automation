package com.efm.efm_backend.repository;

import com.efm.efm_backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEmployeeId(Long employeeId);
    @Query("SELECT d FROM Document d WHERE d.documentType = 'skills'")
    List<Document> findAllSkillDocuments();
}