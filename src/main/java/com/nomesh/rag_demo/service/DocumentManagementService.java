package com.nomesh.rag_demo.service;

import com.nomesh.rag_demo.dto.DocumentInfo;
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

    public DocumentManagementService(
            DocumentStorageService storageService
    ) {
        this.storageService = storageService;
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