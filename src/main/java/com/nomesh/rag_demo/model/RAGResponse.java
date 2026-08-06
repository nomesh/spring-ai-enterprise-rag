package com.nomesh.rag_demo.model;

import java.util.List;

public record RAGResponse(

        String answer,

        List<SourceCitation> sources

) {
}