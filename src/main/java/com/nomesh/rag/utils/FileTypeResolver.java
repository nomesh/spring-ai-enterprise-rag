package com.nomesh.rag.utils;

import org.springframework.stereotype.Component;

@Component
public class FileTypeResolver {

    /**
     * Determines the document type from its filename.
     *
     * <p>The current implementation uses the file extension.
     * This can later be enhanced to support MIME type detection
     * without affecting the ingestion pipeline.</p>
     *
     * @param fileName document filename
     * @return lowercase file extension or {@code "unknown"}
     */
    public String resolve(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1)
                .toLowerCase();
    }
}