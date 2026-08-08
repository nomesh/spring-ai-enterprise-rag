package com.nomesh.rag.search.pagination;

/**
 * Represents the internal candidate window required to satisfy a paginated
 * enterprise search request.
 *
 * <p>The window is an application-level concept and does not expose
 * retrieval-engine-specific APIs to the public search contract.</p>
 *
 * @param offset zero-based result offset within the ranked candidate set
 * @param pageSize maximum number of results returned to the caller
 * @param retrievalLimit number of ranked candidates required from retrieval
 *
 * @author Nomesh De Silva
 */
public record SearchWindow(
        int offset,
        int pageSize,
        int retrievalLimit
) {
}