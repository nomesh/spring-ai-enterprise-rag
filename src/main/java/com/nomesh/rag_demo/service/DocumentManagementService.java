package com.nomesh.rag_demo.service;

import com.nomesh.rag_demo.dto.DocumentInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DocumentManagementService {

    private static final Path DOCUMENT_PATH =
            Paths.get("src/main/resources/documents");

    public List<DocumentInfo> listDocuments() throws IOException {

        try (Stream<Path> files = Files.list(DOCUMENT_PATH)) {

            return files
                    .filter(Files::isRegularFile)
                    .map(this::toDocumentInfo)
                    .toList();
        }
    }

    private DocumentInfo toDocumentInfo(Path path) {

        try {

            return new DocumentInfo(

                    path.getFileName().toString(),

                    path.getFileName().toString(),

                    getExtension(path),

                    Instant.ofEpochMilli(
                            Files.getLastModifiedTime(path).toMillis()
                    ).toString(),

                    "Indexed"

            );

        } catch (IOException e) {

            throw new RuntimeException(e);

        }
    }

    private String getExtension(Path path) {

        String name = path.getFileName().toString();

        int index = name.lastIndexOf('.');

        if (index == -1) {
            return "UNKNOWN";
        }

        return name.substring(index + 1).toUpperCase();
    }

}