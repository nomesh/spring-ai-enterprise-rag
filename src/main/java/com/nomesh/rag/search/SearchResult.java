package com.nomesh.rag.search;

import java.util.Map;

/**
 * Represents a single search result returned by the Enterprise RAG Platform.
 *
 * <p>The model intentionally remains independent from Spring AI so the public
 * search API can remain stable even if the underlying retrieval framework
 * changes in the future.</p>
 *
 * @param content retrieved document content
 * @param score semantic similarity score
 * @param metadata metadata associated with the retrieved document
 *
 * @author Nomesh De Silva
 */
public record SearchResult(
        String content,
        Double score,
        Map<String, Object> metadata
) {
}