package com.nomesh.rag_demo.ingestion.loader;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentLoader {
    List<Document> load();
}
