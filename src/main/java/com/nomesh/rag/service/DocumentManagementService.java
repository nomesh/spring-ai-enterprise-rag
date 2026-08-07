package com.nomesh.rag.service;

import com.nomesh.rag.dto.DocumentDeleteResponse;
import com.nomesh.rag.dto.DocumentInfo;
import com.nomesh.rag.ingestion.indexer.DocumentIndexer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class DocumentManagementService {

    private final DocumentStorageService storageService;
    private final DocumentIndexer documentIndexer;

    public DocumentManagementService(
            DocumentStorageService storageService,
            DocumentIndexer documentIndexer
    ) {
        this.storageService = storageService;
        this.documentIndexer = documentIndexer;
    }

    public List<DocumentInfo> listDocuments() throws IOException {

        Path uploadDirectory =
                storageService.getUploadDirectory();

        try (Stream<Path> files = Files.list(uploadDirectory)) {

            return files
                    .filter(Files::isRegularFile)
                    .map(this::toDocumentInfo)
                    .sorted(
                            Comparator.comparing(
                                    DocumentInfo::uploadedAt
                            ).reversed()
                    )
                    .toList();
        }
    }

    public DocumentDeleteResponse deleteDocument(
            String fileName
    ) throws IOException {

        if (!storageService.exists(fileName)) {
            throw new IllegalArgumentException(
                    "Document does not exist: " + fileName
            );
        }

        /*
         * Remove all indexed chunks belonging to this source.
         */
        documentIndexer.deleteBySource(fileName);

        /*
         * Remove the persisted original file.
         */
        storageService.delete(fileName);

        return new DocumentDeleteResponse(
                fileName,
                "DELETED",
                "Document and indexed chunks deleted successfully."
        );
    }

    private DocumentInfo toDocumentInfo(Path path) {

        try {
            String fileName =
                    path.getFileName().toString();

            return new DocumentInfo(
                    fileName,
                    fileName,
                    getExtension(fileName),
                    Instant.ofEpochMilli(
                            Files.getLastModifiedTime(path)
                                    .toMillis()
                    ).toString(),
                    "INDEXED"
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read document metadata.",
                    exception
            );
        }
    }

    private String getExtension(String fileName) {

        int index = fileName.lastIndexOf('.');

        if (index < 0 || index == fileName.length() - 1) {
            return "UNKNOWN";
        }

        return fileName
                .substring(index + 1)
                .toUpperCase(Locale.ROOT);
    }
}