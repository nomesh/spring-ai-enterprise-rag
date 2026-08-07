package com.nomesh.rag.model;

public record SourceCitation(

        String source,

        String fileType,

        Integer pageNumber,

        Integer chunkNumber,

        Double distance

) {
}