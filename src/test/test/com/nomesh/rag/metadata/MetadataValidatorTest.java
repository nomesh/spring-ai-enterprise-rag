package com.nomesh.rag.metadata;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetadataValidatorTest {

    private final MetadataValidator validator = new MetadataValidator();

    @Test
    void shouldAcceptValidMetadata() {
        DocumentMetadata metadata = validMetadata();

        assertDoesNotThrow(() -> validator.validate(metadata));
    }

    @Test
    void shouldRejectNullMetadata() {
        assertThrows(
                MetadataValidationException.class,
                () -> validator.validate(null)
        );
    }

    @Test
    void shouldRejectMissingDocumentId() {
        DocumentMetadata metadata = new DocumentMetadata(
                null,
                "leave-policy.pdf",
                "pdf",
                "LOCAL_UPLOAD",
                null,
                null,
                List.of(),
                "en",
                null,
                Instant.now(),
                null,
                null,
                null,
                null
        );

        assertThrows(
                MetadataValidationException.class,
                () -> validator.validate(metadata)
        );
    }

    @Test
    void shouldRejectBlankDocumentName() {
        DocumentMetadata metadata = new DocumentMetadata(
                "doc-123",
                " ",
                "pdf",
                "LOCAL_UPLOAD",
                null,
                null,
                List.of(),
                "en",
                null,
                Instant.now(),
                null,
                null,
                null,
                null
        );

        assertThrows(
                MetadataValidationException.class,
                () -> validator.validate(metadata)
        );
    }

    @Test
    void shouldRejectMissingUploadedAt() {
        DocumentMetadata metadata = new DocumentMetadata(
                "doc-123",
                "leave-policy.pdf",
                "pdf",
                "LOCAL_UPLOAD",
                null,
                null,
                List.of(),
                "en",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThrows(
                MetadataValidationException.class,
                () -> validator.validate(metadata)
        );
    }

    private DocumentMetadata validMetadata() {
        return new DocumentMetadata(
                "doc-123",
                "leave-policy.pdf",
                "pdf",
                "LOCAL_UPLOAD",
                "HR Team",
                "HR",
                List.of("leave", "policy"),
                "en",
                "admin",
                Instant.now(),
                "1.0",
                null,
                "INTERNAL",
                "ACTIVE"
        );
    }
}