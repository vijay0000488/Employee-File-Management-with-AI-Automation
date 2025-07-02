package com.efm.efm_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branchName;

    @ManyToOne
    private Branch parentBranch;

    @ManyToOne
    @JoinColumn(name = "project_project_id")
    private Project project;
}