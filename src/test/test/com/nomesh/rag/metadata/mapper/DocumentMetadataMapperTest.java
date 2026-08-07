package com.nomesh.rag.metadata.mapper;

import com.nomesh.rag.metadata.DocumentMetadata;
import com.nomesh.rag.metadata.MetadataConstants;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DocumentMetadataMapperTest {

    private final DocumentMetadataMapper mapper =
            new DocumentMetadataMapper();

    @Test
    void shouldMapAvailableDocumentMetadata() {
        DocumentMetadata metadata = new DocumentMetadata(
                "doc-123",
                "leave-policy.pdf",
                "pdf",
                "LOCAL_UPLOAD",
                "HR Team",
                "HR",
                List.of("leave", "policy"),
                "en",
                "admin",
                Instant.parse("2026-08-07T08:00:00Z"),
                "1.0",
                null,
                "INTERNAL",
                "ACTIVE"
        );

        Map<String, Object> result = mapper.toMap(metadata);

        assertEquals("doc-123", result.get(MetadataConstants.DOCUMENT_ID));
        assertEquals("leave-policy.pdf", result.get(MetadataConstants.DOCUMENT_NAME));
        assertEquals("HR", result.get(MetadataConstants.DEPARTMENT));
        assertEquals(
                List.of("leave", "policy"),
                result.get(MetadataConstants.TAGS)
        );
        assertEquals(
                "2026-08-07T08:00:00Z",
                result.get(MetadataConstants.UPLOADED_AT)
        );
    }

    @Test
    void shouldExcludeMissingOptionalMetadata() {
        DocumentMetadata metadata = new DocumentMetadata(
                "doc-123",
                "leave-policy.pdf",
                "pdf",
                "LOCAL_UPLOAD",
                null,
                null,
                List.of(),
                null,
                null,
                Instant.now(),
                null,
                null,
                null,
                null
        );

        Map<String, Object> result = mapper.toMap(metadata);

        assertFalse(result.containsKey(MetadataConstants.AUTHOR));
        assertFalse(result.containsKey(MetadataConstants.DEPARTMENT));
        assertFalse(result.containsKey(MetadataConstants.TAGS));
        assertFalse(result.containsKey(MetadataConstants.CLASSIFICATION));
    }
}