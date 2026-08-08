package com.nomesh.rag.search.pagination;

import com.nomesh.rag.search.SearchPagination;
import com.nomesh.rag.search.validation.EnterpriseSearchValidationException;
import org.springframework.stereotype.Component;

/**
 * Resolves the internal candidate window required for paginated search.
 *
 * <p>An additional candidate is retrieved beyond the requested page so the
 * platform can determine whether another page may be available without
 * requiring a total result count.</p>
 *
 * @author Nomesh De Silva
 */
@Component
public class SearchWindowResolver {

    public static final int MAX_RETRIEVAL_LIMIT = 100;

    /**
     * Resolves the candidate retrieval window for the supplied pagination.
     *
     * @param pagination resolved pagination containing non-null page and size
     * @return internal search window
     * @throws EnterpriseSearchValidationException when the requested window
     *         exceeds the platform retrieval limit
     */
    public SearchWindow resolve(SearchPagination pagination) {

        int offset = Math.multiplyExact(
                pagination.page(),
                pagination.size()
        );

        int retrievalLimit = Math.addExact(
                Math.addExact(offset, pagination.size()),
                1
        );

        if (retrievalLimit > MAX_RETRIEVAL_LIMIT) {
            throw new EnterpriseSearchValidationException(
                    "Requested pagination window exceeds the maximum retrieval limit of "
                            + MAX_RETRIEVAL_LIMIT + "."
            );
        }

        return new SearchWindow(
                offset,
                pagination.size(),
                retrievalLimit
        );
    }
}