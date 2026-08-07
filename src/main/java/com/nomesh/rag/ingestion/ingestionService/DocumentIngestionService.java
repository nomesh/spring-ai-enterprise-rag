package com.nomesh.rag.ingestion.ingestionService;

import com.nomesh.rag.ingestion.indexer.DocumentIndexer;
import com.nomesh.rag.ingestion.loader.DocumentLoader;
import com.nomesh.rag.ingestion.processor.DocumentMetadataEnricher;
import com.nomesh.rag.ingestion.splitter.DocumentChunker;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final DocumentLoader loader;
    private final DocumentIndexer indexer;
    private final DocumentChunker chunker;
    private final DocumentMetadataEnricher metadataEnricher;

    public DocumentIngestionService(
            DocumentLoader loader,
            DocumentChunker chunker,
            DocumentMetadataEnricher metadataEnricher,
            DocumentIndexer indexer
    ) {
        this.loader = loader;
        this.chunker = chunker;
        this.metadataEnricher = metadataEnricher;
        this.indexer = indexer;
    }

    public int ingest(Resource resource, String sourceFileName) {

        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("Uploaded resource does not exist.");
        }

        if (sourceFileName == null || sourceFileName.isBlank()) {
            throw new IllegalArgumentException("Source file name is required.");
        }

        List<Document> documents = loader.load(resource);

        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException(
                    "No readable content was found in the uploaded document."
            );
        }

        List<Document> chunks = chunker.split(documents);

        List<Document> enrichedChunks =
                metadataEnricher.enrich(chunks, sourceFileName);

        indexer.index(enrichedChunks);

        return enrichedChunks.size();
    }
}