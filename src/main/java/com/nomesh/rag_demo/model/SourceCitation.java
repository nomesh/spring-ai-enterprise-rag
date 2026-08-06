package com.nomesh.rag_demo.model;

public record SourceCitation(

        String source,

        String fileType,

        Integer pageNumber,

        Integer chunkNumber,

        Double distance

) {
}