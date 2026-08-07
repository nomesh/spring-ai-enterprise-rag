package com.nomesh.rag.metadata;

import java.time.Instant;
import java.util.List;

/**
 * Represents the canonical business metadata associated with a logical
 * document managed by the Enterprise RAG Platform.
 *
 * <p>This model provides a source-independent representation of document
 * metadata. File uploads and future enterprise connectors such as
 * Confluence, Jira, SharePoint, and Google Drive should translate their
 * provider-specific metadata into this model before documents enter the
 * ingestion pipeline.</p>
 *
 * <p>The model deliberately represents document-level metadata only.
 * Chunk-specific indexing information, such as chunk number, is added
 * later by the chunking and indexing pipeline.</p>
 *
 * @param documentId     unique identifier of the logical document
 * @param documentName   human-readable document name
 * @param fileType       source document format
 * @param source         logical origin of the document
 * @param author         document author when available
 * @param department     organizational department associated with the document
 * @param tags           document categorization tags
 * @param language       language of the document
 * @param uploadedBy     identity responsible for ingestion
 * @param uploadedAt     timestamp at which the document entered the platform
 * @param version        source document version
 * @param tenantId       tenant owning the document when multi-tenancy is enabled
 * @param classification information classification assigned to the document
 * @param status         document lifecycle status
 *
 * @author Nomesh De Silva
 */
public record DocumentMetadata(
        String documentId,
        String documentName,
        String fileType,
        String source,
        String author,
        String department,
        List<String> tags,
        String language,
        String uploadedBy,
        Instant uploadedAt,
        String version,
        String tenantId,
        String classification,
        String status
) {

    /**
     * Creates immutable defensive state for metadata collections.
     *
     * <p>Metadata may flow through multiple ingestion and retrieval
     * components. Copying tag collections here prevents a caller from
     * modifying document metadata after construction.</p>
     */
    public DocumentMetadata {
        tags = tags == null ? List.of() : List.copyOf(tags);
    }
}