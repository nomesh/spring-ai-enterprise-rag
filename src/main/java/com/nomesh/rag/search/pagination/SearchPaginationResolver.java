package com.nomesh.rag.search.pagination;

import com.nomesh.rag.search.SearchPagination;
import org.springframework.stereotype.Component;

/**
 * Resolves effective pagination settings for enterprise search requests.
 *
 * <p>Centralizing pagination defaults prevents API adapters and retrieval
 * components from duplicating platform-level pagination behavior.</p>
 *
 * @author Nomesh De Silva
 */
@Component
public class SearchPaginationResolver {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    /**
     * Resolves pagination settings using platform defaults when values
     * are not explicitly supplied.
     *
     * @param pagination pagination requested by the caller, or {@code null}
     * @return resolved pagination containing non-null page and size values
     */
    public SearchPagination resolve(SearchPagination pagination) {

        if (pagination == null) {
            return new SearchPagination(DEFAULT_PAGE, DEFAULT_SIZE);
        }

        int page = pagination.page() == null
                ? DEFAULT_PAGE
                : pagination.page();

        int size = pagination.size() == null
                ? DEFAULT_SIZE
                : pagination.size();

        return new SearchPagination(page, size);
    }
}