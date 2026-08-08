package com.nomesh.rag.metadata.mapper;

import com.nomesh.rag.metadata.DocumentMetadata;
import com.nomesh.rag.utils.FileTypeResolver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileMetadataMapperTest {

    private final FileTypeResolver fileTypeResolver =
            new FileTypeResolver();

    private final LocalFileMetadataMapper mapper =
            new LocalFileMetadataMapper(fileTypeResolver);

    @Test
    void shouldCreateMetadataForLocalFile() {

        DocumentMetadata metadata =
                mapper.map("employee-handbook.pdf");

        assertNotNull(metadata.documentId());
        assertEquals(
                "employee-handbook.pdf",
                metadata.documentName()
        );
        assertEquals(
                "pdf",
                metadata.fileType()
        );
        assertNotNull(metadata.uploadedAt());
    }

    @Test
    void shouldUseUnknownWhenFileHasNoExtension() {

        DocumentMetadata metadata =
                mapper.map("README");

        assertEquals(
                "unknown",
                metadata.fileType()
        );
    }
}