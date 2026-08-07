package com.nomesh.rag.metadata;

import org.springframework.stereotype.Component;

/**
 * Validates document metadata before it enters the indexing pipeline.
 *
 * <p>Validation protects the vector store from incomplete metadata that
 * could later break filtering, deletion, citations, or document lifecycle
 * operations.</p>
 */
@Component
public class MetadataValidator {

    /**
     * Validates the required fields of a document metadata instance.
     *
     * <p>Only fields required by the current platform are enforced here.
     * Optional enterprise fields such as department, tenant, author, and
     * classification can be introduced gradually without blocking ingestion.</p>
     *
     * @param metadata metadata to validate
     * @throws MetadataValidationException if required metadata is missing
     */
    public void validate(DocumentMetadata metadata) {
        if (metadata == null) {
            throw new MetadataValidationException("Document metadata must not be null.");
        }

        requireText(metadata.documentId(), MetadataConstants.DOCUMENT_ID);
        requireText(metadata.documentName(), MetadataConstants.DOCUMENT_NAME);
        requireText(metadata.fileType(), MetadataConstants.FILE_TYPE);
        requireText(metadata.source(), MetadataConstants.SOURCE);

        if (metadata.uploadedAt() == null) {
            throw new MetadataValidationException(
                    MetadataConstants.UPLOADED_AT + " must not be null."
            );
        }
    }

    /**
     * Ensures that a required text field contains a meaningful value.
     *
     * @param value metadata value
     * @param fieldName metadata field name used in the validation message
     */
    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new MetadataValidationException(
                    fieldName + " must not be blank."
            );
        }
    }
}