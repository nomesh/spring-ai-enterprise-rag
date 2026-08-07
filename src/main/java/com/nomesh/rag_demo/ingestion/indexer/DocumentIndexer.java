package com.nomesh.rag_demo.ingestion.indexer;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIndexer {

    private static final String SOURCE_METADATA_KEY = "source";

    private final VectorStore vectorStore;

    public DocumentIndexer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void index(List<Document> documents) {

        if (documents == null || documents.isEmpty()) {
            return;
        }

        vectorStore.add(documents);
    }

    public void deleteBySource(String sourceFileName) {

        if (sourceFileName == null || sourceFileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Source filename is required."
            );
        }

        Filter.Expression filterExpression =
                new FilterExpressionBuilder()
                        .eq(
                                SOURCE_METADATA_KEY,
                                sourceFileName
                        )
                        .build();

        vectorStore.delete(filterExpression);
    }
}