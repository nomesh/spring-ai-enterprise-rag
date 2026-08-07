package com.nomesh.rag.retrieval;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${rag.retrieval.top-k}")
    private int topK;

    @Value("${rag.retrieval.similarity-threshold}")
    private double similarityThreshold; //Retrieval quality is controlled more by search configuration than by the LLM.

    public DocumentRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> retrieve(String question) {

        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build();

        return vectorStore.similaritySearch(request);
    }
}


/*
 Retrieval quality is controlled more by search configuration than by the LLM.
 * Score  | 	Interpretation
 * -------------------------
 * > 0.90	|  Excellent match
 * 0.80–0.90|  Very good
 * 0.70–0.80|  Good
 * 0.60–0.70|  Acceptable
 * < 0.50	|  Usually irrelevant
 */
