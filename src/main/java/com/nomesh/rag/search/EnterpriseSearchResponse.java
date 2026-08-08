package com.nomesh.rag.search;

import java.util.List;

/**
 * Represents the response returned by enterprise search operations.
 *
 * <p>The response is independent from Spring AI and exposes only platform-level
 * search concepts required by API consumers.</p>
 *
 * @param query original search query
 * @param results matching search results
 * @param pagination pagination metadata for the returned result window
 *
 * @author Nomesh De Silva
 */
public record EnterpriseSearchResponse(
        String query,
        List<SearchResult> results,
        SearchPageMetadata pagination
) {

    /**
     * Creates immutable search result state.
     */
    public EnterpriseSearchResponse {
        results = results == null
                ? List.of()
                : List.copyOf(results);
    }
}