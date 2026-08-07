package com.nomesh.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class DocumentStorageService {

    private final Path uploadDirectory;

    public DocumentStorageService(
            @Value("${rag.storage.upload-directory:uploads}")
            String uploadDirectory
    ) {
        this.uploadDirectory =
                Paths.get(uploadDirectory)
                        .toAbsolutePath()
                        .normalize();

        createUploadDirectory();
    }

    public Path store(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Uploaded file must have a valid filename."
            );
        }

        String cleanFileName =
                validateAndCleanFileName(originalFileName);

        Path targetPath =
                uploadDirectory
                        .resolve(cleanFileName)
                        .normalize();

        if (!targetPath.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException(
                    "File must be stored inside the upload directory."
            );
        }

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return targetPath;
    }

    public Resource loadAsResource(Path path) {

        try {
            Resource resource =
                    new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException(
                        "Stored file cannot be read: " + path.getFileName()
                );
            }

            return resource;

        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException(
                    "Stored file path is invalid.",
                    exception
            );
        }
    }

    public Path getUploadDirectory() {
        return uploadDirectory;
    }

    private void createUploadDirectory() {

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create document upload directory.",
                    exception
            );
        }
    }

    public Path resolve(String fileName) {

        String cleanFileName =
                validateAndCleanFileName(fileName);

        Path resolvedPath =
                uploadDirectory
                        .resolve(cleanFileName)
                        .normalize();

        if (!resolvedPath.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException(
                    "Invalid document path."
            );
        }

        return resolvedPath;
    }

    public boolean exists(String fileName) {

        Path filePath = resolve(fileName);

        return Files.exists(filePath)
                && Files.isRegularFile(filePath);
    }

    public void delete(String fileName) throws IOException {

        Path filePath = resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException(
                    "Document does not exist: " + fileName
            );
        }

        Files.delete(filePath);
    }

    private String validateAndCleanFileName(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Document filename is required."
            );
        }

        String cleanFileName =
                StringUtils.cleanPath(fileName);

        if (cleanFileName.contains("..")
                || cleanFileName.contains("/")
                || cleanFileName.contains("\\")) {

            throw new IllegalArgumentException(
                    "Invalid document filename."
            );
        }

        return cleanFileName;
    }
}