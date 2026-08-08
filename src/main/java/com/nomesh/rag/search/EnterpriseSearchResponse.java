package com.nomesh.rag.search;

import java.util.List;

/**
 * Represents the response returned by enterprise semantic search operations.
 *
 * <p>The response is independent from Spring AI and exposes only platform-level
 * search concepts required by API consumers.</p>
 *
 * @param query original search query
 * @param totalResults number of returned search results
 * @param results matching search results
 *
 * @author Nomesh De Silva
 */
public record EnterpriseSearchResponse(
        String query,
        int totalResults,
        List<SearchResult> results
) {

    /**
     * Creates immutable search result state.
     */
    public EnterpriseSearchResponse {
        results = results == null ? List.of() : List.copyOf(results);
    }
}