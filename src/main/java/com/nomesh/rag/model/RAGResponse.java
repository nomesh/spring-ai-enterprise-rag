package com.nomesh.rag.model;

import java.util.List;

public record RAGResponse(

        String answer,

        List<SourceCitation> sources

) {
}