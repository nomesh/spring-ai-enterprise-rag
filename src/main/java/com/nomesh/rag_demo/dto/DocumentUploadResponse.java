package com.nomesh.rag_demo.dto;


public record DocumentUploadResponse(
        String fileName,
        String status,
        int indexedChunks,
        String message
) {
}