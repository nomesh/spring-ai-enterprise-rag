package com.nomesh.rag.ingestion.processor;

import com.nomesh.rag.model.MetaDataKeys;
import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * retrieval quality depends heavily on good document preparation.
 * Before implementing retrieval, we want to make sure the data entering PGVector is enterprise-ready.
 * @author Nomesh De Silva
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

            document.getMetadata().put(MetaDataKeys.SOURCE,sourceFileName );
            document.getMetadata().put(MetaDataKeys.FILE_TYPE, getFileExtension(sourceFileName));
            document.getMetadata().put(MetaDataKeys.INGESTED_AT, Instant.now().toString());

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
