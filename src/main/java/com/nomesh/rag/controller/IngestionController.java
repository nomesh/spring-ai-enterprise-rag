package com.nomesh.rag.controller;

import com.nomesh.rag.dto.DocumentUploadResponse;
import com.nomesh.rag.ingestion.ingestionService.DocumentIngestionService;
import com.nomesh.rag.service.DocumentStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/documents")
public class IngestionController {

    private static final Set<String> SUPPORTED_EXTENSIONS =
            Set.of("txt");

    private final DocumentIngestionService ingestionService;
    private final DocumentStorageService storageService;

    public IngestionController(DocumentIngestionService ingestionService,DocumentStorageService storageService
    ) {
        this.ingestionService = ingestionService;
        this.storageService = storageService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    public ResponseEntity<DocumentUploadResponse> upload(@RequestParam("file") MultipartFile file
    ) throws IOException {

        validateFile(file);

        String originalFileName =
                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        Path storedPath = storageService.store(file);

        Resource storedResource =
                storageService.loadAsResource(storedPath);

        int indexedChunks =
                ingestionService.ingest(
                        storedResource,
                        originalFileName
                );

        DocumentUploadResponse response =
                new DocumentUploadResponse(
                        originalFileName,
                        "INDEXED",
                        indexedChunks,
                        "Document uploaded and indexed successfully."
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a non-empty file."
            );
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Uploaded file must have a valid filename."
            );
        }

        String extension = getFileExtension(originalFileName);

        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Unsupported file type. Currently supported: TXT."
            );
        }
    }

    private String getFileExtension(String fileName) {

        int index = fileName.lastIndexOf('.');

        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }

        return fileName
                .substring(index + 1)
                .toLowerCase(Locale.ROOT);
    }
}