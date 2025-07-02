package com.efm.efm_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    private String fileName;
    private String fileType;
    private Long fileSize;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] data;

    private String documentType; // e.g., "resume", "certificate", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;

    @Version
    private Long version;


}