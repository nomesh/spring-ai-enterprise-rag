package com.nomesh.rag.search;

import com.nomesh.rag.search.filter.DocumentSearchFilter;

/**
 * Represents a search request handled by the Enterprise RAG Platform.
 *
 * <p>The request keeps search options independent from Spring AI or the
 * underlying vector database, allowing the search layer to evolve without
 * changing the public search contract.</p>
 *
 * @param query search text
 * @param topK maximum number of results to return
 * @param similarityThreshold minimum semantic similarity score
 * @param filter optional metadata filters used to narrow the search
 */
public record EnterpriseSearchRequest(
        String query,
        Integer topK,
        Double similarityThreshold,
        DocumentSearchFilter filter
) {
}