package com.efm.efm_backend.repository;

import com.efm.efm_backend.entity.Branch;
import com.efm.efm_backend.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    List<ProjectFile> findByProject_ProjectId(Long projectId);
    List<ProjectFile> findByProject_ProjectIdAndBranch_Id(Long projectId, Long branchId);
    List<ProjectFile> findByProject_ProjectIdAndBranch_BranchName(Long projectId, String branchName);
    List<ProjectFile> findByUploadedBy_Id(Long employeeId);
    List<ProjectFile> findByBranch_BranchName(String branchName);
    List<ProjectFile> findAll();



}
