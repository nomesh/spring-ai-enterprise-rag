package com.nomesh.rag_demo.ingestion.indexer;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIndexer {

    private final VectorStore vectorStore;

    @Autowired
    public DocumentIndexer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    public void index(List<Document> documents){

        vectorStore.add(documents);

    }
}