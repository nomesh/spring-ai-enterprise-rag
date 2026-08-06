package com.nomesh.rag_demo.model;

public record SourceCitation(

        String documentName,

        String documentType,

        String pageNumber,

        String chunkId,

        Double similarityScore

) {
}