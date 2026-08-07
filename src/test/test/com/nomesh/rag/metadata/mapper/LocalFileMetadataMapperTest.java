package com.nomesh.rag.metadata.mapper;

import com.nomesh.rag.metadata.DocumentMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileMetadataMapperTest {

    private final LocalFileMetadataMapper mapper =
            new LocalFileMetadataMapper();

    @Test
    void shouldCreateMetadataForUploadedFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "employee-handbook.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        DocumentMetadata metadata = mapper.map(file);

        assertNotNull(metadata.documentId());
        assertEquals("employee-handbook.pdf", metadata.documentName());
        assertEquals("pdf", metadata.fileType());
        assertEquals("LOCAL_UPLOAD", metadata.source());
        assertNotNull(metadata.uploadedAt());
        assertTrue(metadata.tags().isEmpty());
    }

    @Test
    void shouldUseUnknownWhenFileHasNoExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "README",
                "text/plain",
                "test content".getBytes()
        );

        DocumentMetadata metadata = mapper.map(file);

        assertEquals("unknown", metadata.fileType());
    }
}