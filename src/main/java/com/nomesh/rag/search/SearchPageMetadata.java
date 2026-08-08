package com.nomesh.rag.search;

/**
 * Describes pagination metadata returned with enterprise search results.
 *
 * <p>The metadata is independent from Spring Data and retrieval-engine-specific
 * paging models so the public search contract remains portable across search
 * technologies.</p>
 *
 * @param page zero-based page represented by the response
 * @param size configured maximum number of results for the page
 * @param returnedResults number of results actually returned
 * @param hasMore whether additional results may be available
 *
 * @author Nomesh De Silva
 */
public record SearchPageMetadata(
        int page,
        int size,
        int returnedResults,
        boolean hasMore
) {
}