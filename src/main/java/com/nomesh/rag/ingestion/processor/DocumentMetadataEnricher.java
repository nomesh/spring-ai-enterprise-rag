package com.nomesh.rag.ingestion.processor;

import com.nomesh.rag.metadata.DocumentMetadata;
import com.nomesh.rag.metadata.mapper.DocumentMetadataMapper;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Adds standard document metadata to generated document chunks.
 *
 * <p>Each chunk keeps the same document-level metadata so filtering,
 * citations, and document lifecycle operations can work consistently
 * across the whole document.</p>
 */
@Component
public class DocumentMetadataEnricher {

    private final DocumentMetadataMapper metadataMapper;

    public DocumentMetadataEnricher(DocumentMetadataMapper metadataMapper) {
        this.metadataMapper = metadataMapper;
    }

    /**
     * Adds document metadata to every chunk created from the same source.
     *
     * @param documents chunks generated from the source document
     * @param metadata standard metadata describing the source document
     * @return enriched document chunks
     */
    public List<Document> enrich(
            List<Document> documents,
            DocumentMetadata metadata
    ) {
        Map<String, Object> documentMetadata = metadataMapper.toMap(metadata);

        for (Document document : documents) {
            document.getMetadata().putAll(documentMetadata);
        }

        return documents;
    }
}