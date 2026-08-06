package com.nomesh.rag_demo.retrieval;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retrieval (R) - Perform the similarity search for the documents requested via query prompt.
 * and search for requested documents in vector store.

 * Notice something important:
 * You did not search for the exact words.
 * You searched for the meaning.
  * That's why semantic search is so powerful.
 *
 * @author Nomesh De Silva
 */
@Component
public class DocumentRetriever {

    private final VectorStore vectorStore;

    public DocumentRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> retrieve(String question) {

        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .build();

        return vectorStore.similaritySearch(request);
    }
}
