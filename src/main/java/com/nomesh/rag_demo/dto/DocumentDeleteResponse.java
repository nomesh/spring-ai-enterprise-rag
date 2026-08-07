package com.nomesh.rag_demo.dto;

public record DocumentDeleteResponse(
        String fileName,
        String status,
        String message
) {
}