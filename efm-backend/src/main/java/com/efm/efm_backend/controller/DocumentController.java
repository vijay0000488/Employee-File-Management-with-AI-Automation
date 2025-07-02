package com.efm.efm_backend.controller;

import com.efm.efm_backend.entity.Document;
import com.efm.efm_backend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload/{employeeId}")
    public ResponseEntity<Document> uploadFile(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType
    ) throws IOException {
        Document doc = documentService.uploadDocument(employeeId, file, documentType);
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long documentId) {
        Document document = documentService.getDocument(documentId);
        ByteArrayResource resource = new ByteArrayResource(document.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/update/{documentId}")
    public ResponseEntity<Document> updateFile(
            @PathVariable Long documentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType
    ) throws IOException {
        Document doc = documentService.updateDocument(documentId, file, documentType);
        return ResponseEntity.ok(doc);
    }

    @DeleteMapping("/delete/{documentId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Document>> getDocsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(documentService.getDocumentsByEmployee(employeeId));
    }
}