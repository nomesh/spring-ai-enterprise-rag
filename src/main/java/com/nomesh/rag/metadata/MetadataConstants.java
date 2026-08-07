package com.nomesh.rag.metadata;

/**
 * Defines the canonical metadata keys used for documents throughout the
 * Enterprise RAG Platform.
 *
 * <p>Centralizing metadata keys prevents individual ingestion, retrieval,
 * filtering, and integration components from creating incompatible metadata
 * representations. These keys form part of the internal document metadata
 * contract and should therefore be changed only with consideration for
 * persisted vector-store data and backward compatibility.</p>
 *
 * <p>Future document sources such as Confluence, Jira, SharePoint, Google
 * Drive, and local file uploads should map their source-specific metadata
 * into these canonical keys before documents enter the retrieval pipeline.</p>
 *
 * @author Nomesh De Silva
 */
public final class MetadataConstants {

    /**
     * Unique identifier representing the logical source document.
     *
     * <p>All chunks originating from the same document should retain the
     * same document identifier so that lifecycle operations such as deletion,
     * replacement, filtering, and citation aggregation can operate at
     * document level.</p>
     */
    public static final String DOCUMENT_ID = "documentId";

    /**
     * Human-readable name of the source document.
     */
    public static final String DOCUMENT_NAME = "documentName";

    /**
     * Type or format of the source document, such as PDF, DOCX, or TXT.
     */
    public static final String FILE_TYPE = "fileType";

    /**
     * Logical origin from which the document was obtained.
     *
     * <p>Examples include LOCAL_UPLOAD, CONFLUENCE, JIRA, SHAREPOINT,
     * and GOOGLE_DRIVE.</p>
     */
    public static final String SOURCE = "source";

    /**
     * Author or owner associated with the source document when available.
     */
    public static final String AUTHOR = "author";

    /**
     * Organizational department associated with the document.
     */
    public static final String DEPARTMENT = "department";

    /**
     * Tags associated with the document for categorization and retrieval.
     */
    public static final String TAGS = "tags";

    /**
     * Language in which the document content is written.
     */
    public static final String LANGUAGE = "language";

    /**
     * Identity of the user or system that ingested the document.
     */
    public static final String UPLOADED_BY = "uploadedBy";

    /**
     * Timestamp at which the document entered the RAG platform.
     */
    public static final String UPLOADED_AT = "uploadedAt";

    /**
     * Version of the logical source document.
     */
    public static final String VERSION = "version";

    /**
     * Tenant owning the document.
     *
     * <p>This field is reserved for multi-tenant isolation and should not
     * currently be interpreted as authorization enforcement until the
     * security model is implemented.</p>
     */
    public static final String TENANT_ID = "tenantId";

    /**
     * Information classification assigned to the document.
     *
     * <p>Examples may eventually include PUBLIC, INTERNAL,
     * CONFIDENTIAL, and RESTRICTED.</p>
     */
    public static final String CLASSIFICATION = "classification";

    /**
     * Lifecycle status of the document.
     */
    public static final String STATUS = "status";

    /**
     * Sequential number of a chunk within its source document.
     */
    public static final String CHUNK_NUMBER = "chunkNumber";

    /**
     * Total number of chunks generated from the source document.
     */
    public static final String TOTAL_CHUNKS = "totalChunks";

    /**
     * Prevents instantiation because this class represents a constants-only
     * metadata contract.
     */
    private MetadataConstants() {
    }
}