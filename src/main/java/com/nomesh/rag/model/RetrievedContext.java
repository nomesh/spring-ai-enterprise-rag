package com.nomesh.rag.model;


import org.springframework.ai.document.Document;

import java.util.List;


public record RetrievedContext(

       List<Document> documents,
       List<SourceCitation> citations

) {
}