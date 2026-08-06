package com.nomesh.rag_demo.model;

public record SourceCitation(
        String documentName,
        String pageNumber,
        double similarityScore
) {
}
