package com.efm.efm_backend.controller;

import com.efm.efm_backend.entity.Branch;
import com.efm.efm_backend.entity.Project;
import com.efm.efm_backend.repository.BranchRepository;
import com.efm.efm_backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {
    @Autowired
    private BranchRepository branchRepo;
    @Autowired
    private ProjectRepository projectRepo;

    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody Branch branch, @RequestParam Long projectId) {
        Optional<Project> projectOpt = projectRepo.findById(projectId);
        if (projectOpt.isEmpty()) return ResponseEntity.badRequest().body("Project not found.");
        branch.setProject(projectOpt.get());
        Branch saved = branchRepo.save(branch);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Branch> getAllBranches() {
        return branchRepo.findAll();
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<?> getBranch(@PathVariable Long branchId) {
        return branchRepo.findById(branchId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{branchId}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long branchId) {
        if (!branchRepo.existsById(branchId)) return ResponseEntity.notFound().build();
        branchRepo.deleteById(branchId);
        return ResponseEntity.ok().build();
    }
}