package com.nomesh.rag.metadata.mapper;

import com.nomesh.rag.metadata.DocumentMetadata;
import com.nomesh.rag.metadata.MetadataConstants;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts document metadata into the format used by Spring AI documents.
 *
 * <p>This keeps the platform's metadata model independent from the
 * storage and retrieval framework used underneath it.</p>
 */
@Component
public class DocumentMetadataMapper {

    /**
     * Converts document metadata into a map that can be attached to
     * a Spring AI document.
     *
     * <p>Optional values are added only when they are available so the
     * vector store contains meaningful metadata rather than placeholder values.</p>
     *
     * @param metadata document metadata to convert
     * @return metadata values ready for Spring AI
     */
    public Map<String, Object> toMap(DocumentMetadata metadata) {
        Map<String, Object> values = new HashMap<>();

        put(values, MetadataConstants.DOCUMENT_ID, metadata.documentId());
        put(values, MetadataConstants.DOCUMENT_NAME, metadata.documentName());
        put(values, MetadataConstants.FILE_TYPE, metadata.fileType());
        put(values, MetadataConstants.SOURCE, metadata.source());
        put(values, MetadataConstants.AUTHOR, metadata.author());
        put(values, MetadataConstants.DEPARTMENT, metadata.department());
        put(values, MetadataConstants.LANGUAGE, metadata.language());
        put(values, MetadataConstants.UPLOADED_BY, metadata.uploadedBy());
        put(values, MetadataConstants.VERSION, metadata.version());
        put(values, MetadataConstants.TENANT_ID, metadata.tenantId());
        put(values, MetadataConstants.CLASSIFICATION, metadata.classification());
        put(values, MetadataConstants.STATUS, metadata.status());

        if (!metadata.tags().isEmpty()) {
            values.put(MetadataConstants.TAGS, metadata.tags());
        }

        if (metadata.uploadedAt() != null) {
            values.put(
                    MetadataConstants.UPLOADED_AT,
                    metadata.uploadedAt().toString()
            );
        }

        return values;
    }

    /**
     * Adds a metadata value only when it contains useful text.
     *
     * <p>This avoids storing empty strings that could later behave like
     * valid values during metadata filtering.</p>
     *
     * @param metadata target metadata map
     * @param key metadata key
     * @param value metadata value
     */
    private void put(Map<String, Object> metadata, String key, String value) {
        if (value != null && !value.isBlank()) {
            metadata.put(key, value);
        }
    }
}