package com.nomesh.rag.dto;

public record DocumentDeleteResponse(
        String fileName,
        String status,
        String message
) {
}