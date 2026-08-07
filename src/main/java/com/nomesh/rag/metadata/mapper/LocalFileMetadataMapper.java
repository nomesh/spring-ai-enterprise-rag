package com.nomesh.rag.metadata.mapper;

import com.nomesh.rag.metadata.DocumentMetadata;
import com.nomesh.rag.utils.FileTypeResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Creates standard document metadata for files uploaded directly to the platform.
 *
 * <p>It converts information available from a local file upload into the
 * common {@link DocumentMetadata} model used by the rest of the application.</p>
 */
@Component
public class LocalFileMetadataMapper {

    private static final String SOURCE_LOCAL_UPLOAD = "LOCAL_UPLOAD";
    private final FileTypeResolver fileTypeResolver;

    public LocalFileMetadataMapper(FileTypeResolver fileTypeResolver) {
        this.fileTypeResolver = fileTypeResolver;
    }

    /**
     * Creates document metadata from an uploaded file.
     *
     * <p>Metadata that cannot be determined from the file itself remains empty
     * and can be enriched later by the user or another processing step.</p>
     *
     * @param file uploaded document
     * @return standard metadata for the document
     */
    public DocumentMetadata map(String sourceFileName) {

        return new DocumentMetadata(
                UUID.randomUUID().toString(),
                sourceFileName,
                fileTypeResolver.resolve(sourceFileName),
                sourceFileName,   // We'll improve this in the next story
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
    }

    /**
     * Determines the document type from the uploaded filename.
     *
     * <p>The file extension is used because it provides a simple and consistent
     * value for filtering while the platform supports local file uploads.</p>
     *
     * @param file uploaded document
     * @return lowercase file extension, or {@code "unknown"} when unavailable
     */
    private String resolveFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            return "unknown";
        }

        return filename.substring(filename.lastIndexOf('.') + 1)
                .toLowerCase();
    }
}