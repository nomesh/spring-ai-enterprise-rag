package com.nomesh.rag.controller;

import com.nomesh.rag.dto.DocumentDeleteResponse;
import com.nomesh.rag.dto.DocumentInfo;
import com.nomesh.rag.service.DocumentManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentManagementController {

    private final DocumentManagementService service;

    public DocumentManagementController(
            DocumentManagementService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentInfo> listDocuments()
            throws IOException {

        return service.listDocuments();
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<DocumentDeleteResponse> deleteDocument(
            @PathVariable String fileName
    ) throws IOException {

        DocumentDeleteResponse response =
                service.deleteDocument(fileName);

        return ResponseEntity.ok(response);
    }
}