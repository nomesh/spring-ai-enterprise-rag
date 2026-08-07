package com.nomesh.rag.ingestion.ingestionService;

import com.nomesh.rag.ingestion.indexer.DocumentIndexer;
import com.nomesh.rag.ingestion.loader.DocumentLoader;
import com.nomesh.rag.ingestion.processor.DocumentMetadataEnricher;
import com.nomesh.rag.ingestion.splitter.DocumentChunker;
import com.nomesh.rag.metadata.DocumentMetadata;
import com.nomesh.rag.metadata.MetadataValidator;
import com.nomesh.rag.metadata.mapper.LocalFileMetadataMapper;
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
    private final LocalFileMetadataMapper localFileMetadataMapper;
    private final MetadataValidator metadataValidator;

    public DocumentIngestionService(
            DocumentLoader loader,
            DocumentChunker chunker,
            DocumentMetadataEnricher metadataEnricher,
            DocumentIndexer indexer,
            LocalFileMetadataMapper localFileMetadataMapper,
            MetadataValidator metadataValidator
    ) {
        this.loader = loader;
        this.chunker = chunker;
        this.metadataEnricher = metadataEnricher;
        this.indexer = indexer;
        this.localFileMetadataMapper = localFileMetadataMapper;
        this.metadataValidator = metadataValidator;
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

        DocumentMetadata metadata =
                localFileMetadataMapper.map(sourceFileName);

        metadataValidator.validate(metadata);

        List<Document> enrichedChunks =
                metadataEnricher.enrich(chunks, metadata);

        indexer.index(enrichedChunks);

        return enrichedChunks.size();
    }
}