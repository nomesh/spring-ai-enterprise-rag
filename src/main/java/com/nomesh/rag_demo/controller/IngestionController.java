package com.nomesh.rag_demo.controller;

import com.nomesh.rag_demo.ingestion.ingestionService.DocumentIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final DocumentIngestionService service;

    public IngestionController(DocumentIngestionService service) {
        this.service = service;
    }


    @PostMapping
    public String ingest() {
        service.ingest();
        return "Document indexed successfully";
    }
}