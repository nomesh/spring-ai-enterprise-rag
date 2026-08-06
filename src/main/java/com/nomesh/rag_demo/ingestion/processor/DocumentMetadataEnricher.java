package com.nomesh.rag_demo.ingestion.processor;

import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * retrieval quality depends heavily on good document preparation.
 * Before implementing retrieval, we want to make sure the data entering PGVector is enterprise-ready.
 * @authoer Nomesh De Silva
 *
 */
    @Component
    public class DocumentMetadataEnricher {

    /**
     * Fetch documents enriched with meta data for ease of filtering.
     * @param documents
     * @param sourceFileName
     * @return
     */
    public List<Document> enrich(List<Document> documents, String sourceFileName) {

        List<Document> enrichedDocuments = new ArrayList<>();

        for (Document document : documents) {

            document.getMetadata().put("source", sourceFileName);

            document.getMetadata().put(
                    "fileType",
                    getFileExtension(sourceFileName)
            );

            document.getMetadata().put(
                    "ingestedAt",
                    LocalDateTime.now().toString()
            );

            document.getMetadata().put("source", sourceFileName);
            document.getMetadata().put("fileType", getFileExtension(sourceFileName));
            document.getMetadata().put("ingestedAt", Instant.now().toString());

            enrichedDocuments.add(document);
        }

        return enrichedDocuments;
    }

    private String getFileExtension(String fileName) {

        int index = fileName.lastIndexOf('.');

        if (index == -1) {
            return "UNKNOWN";
        }

        return fileName.substring(index + 1).toUpperCase();
    }
}
