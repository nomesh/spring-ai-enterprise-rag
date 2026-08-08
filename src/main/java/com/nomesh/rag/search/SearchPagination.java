package com.nomesh.rag.search;

/**
 * Represents pagination preferences for enterprise search results.
 *
 * <p>The pagination contract remains independent from Spring Data and
 * retrieval-engine-specific pagination models so the public search API can
 * evolve independently from the underlying search technology.</p>
 *
 * @param page zero-based result page requested by the caller
 * @param size maximum number of results to return on the requested page
 *
 * @author Nomesh De Silva
 */
public record SearchPagination(
        Integer page,
        Integer size
) {
}