package com.efm.efm_backend.repository;

import com.efm.efm_backend.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    // You can add custom query methods if needed
}