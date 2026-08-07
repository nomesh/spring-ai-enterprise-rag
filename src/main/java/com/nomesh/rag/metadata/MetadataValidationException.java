package com.nomesh.rag.metadata;

/**
 * Thrown when document metadata does not meet the minimum requirements
 * needed by the ingestion and search pipelines.
 *
 * <p>This exception keeps metadata validation errors separate from
 * storage or parsing failures, making problems easier to diagnose.</p>
 */
public class MetadataValidationException extends RuntimeException {

    /**
     * Creates a validation exception with a clear error message.
     *
     * @param message description of the validation failure
     */
    public MetadataValidationException(String message) {
        super(message);
    }
}