package com.nomesh.rag.dto;


public record DocumentUploadResponse(
        String fileName,
        String status,
        int indexedChunks,
        String message
) {
}