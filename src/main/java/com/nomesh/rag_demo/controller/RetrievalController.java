package com.nomesh.rag_demo.controller;

import com.nomesh.rag_demo.retrieval.DocumentRetriever;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller calls to perform the semantic retrieval
 * @author Nomesh De Silva
 */
@RestController
@RequestMapping("/api/retrieval")
public class RetrievalController {

    private final DocumentRetriever retriever;

    public RetrievalController(DocumentRetriever retriever) {
        this.retriever = retriever;
    }

    @GetMapping
    public List<Document> retrieve(@RequestParam String question) {
        return retriever.retrieve(question);
    }
}
