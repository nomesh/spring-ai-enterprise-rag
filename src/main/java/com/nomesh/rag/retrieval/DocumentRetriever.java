package com.nomesh.rag.retrieval;

import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.filter.MetadataFilterTranslator;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retrieves relevant documents from the vector store.
 *
 * <p>The retriever converts the platform's search request into the format
 * required by Spring AI while keeping the rest of the application independent
 * from the underlying vector-store API.</p>
 */
@Component
public class DocumentRetriever {

    private final VectorStore vectorStore;
    private final MetadataFilterTranslator filterTranslator;

    @Value("${rag.retrieval.top-k}")
    private int defaultTopK;

    @Value("${rag.retrieval.similarity-threshold}")
    private double defaultSimilarityThreshold;

    /**
     * Creates a document retriever with the required search components.
     *
     * @param vectorStore vector store used for semantic retrieval
     * @param filterTranslator converts metadata filters into Spring AI expressions
     */
    public DocumentRetriever(
            VectorStore vectorStore,
            MetadataFilterTranslator filterTranslator
    ) {
        this.vectorStore = vectorStore;
        this.filterTranslator = filterTranslator;
    }

    /**
     * Retrieves documents using the supplied enterprise search request.
     *
     * <p>Platform defaults are used when result count or similarity threshold
     * are not explicitly provided by the caller.</p>
     *
     * @param request enterprise search request
     * @return relevant documents matching the search criteria
     */
    public List<Document> retrieve(EnterpriseSearchRequest request) {

        return retrieve(
                request,
                resolveTopK(request)
        );
    }

    /**
     * Retrieves documents using the default semantic search configuration.
     *
     * <p>This method preserves the existing retrieval API while callers
     * gradually migrate to the enterprise search contract.</p>
     *
     * @param question search query
     * @return relevant documents ordered by semantic similarity
     */
    public List<Document> retrieve(String question) {

        return retrieve(
                new EnterpriseSearchRequest(
                        question,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    private int resolveTopK(EnterpriseSearchRequest request) {
        return request.topK() != null
                ? request.topK()
                : defaultTopK;
    }

    private double resolveSimilarityThreshold(
            EnterpriseSearchRequest request
    ) {
        return request.similarityThreshold() != null
                ? request.similarityThreshold()
                : defaultSimilarityThreshold;
    }

    /**
     * Retrieves documents using the supplied enterprise search request and
     * candidate retrieval limit.
     *
     * @param request enterprise search request
     * @param retrievalLimit maximum number of ranked candidates to retrieve
     * @return relevant documents ordered by semantic similarity
     */
    public List<Document> retrieve(
            EnterpriseSearchRequest request,
            int retrievalLimit
    ) {

        SearchRequest.Builder searchRequest = SearchRequest.builder()
                .query(request.query())
                .topK(retrievalLimit)
                .similarityThreshold(resolveSimilarityThreshold(request));

        filterTranslator.translate(request.filter())
                .ifPresent(searchRequest::filterExpression);

        return vectorStore.similaritySearch(
                searchRequest.build()
        );
    }
}