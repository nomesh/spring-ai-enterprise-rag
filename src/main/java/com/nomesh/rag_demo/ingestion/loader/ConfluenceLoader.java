package com.nomesh.rag_demo.ingestion.loader;

import org.springframework.ai.document.Document;

import java.util.List;

public class ConfluenceLoader implements DocumentLoader{
    @Override
    public List<Document> load() {
        return List.of();
    }
}
