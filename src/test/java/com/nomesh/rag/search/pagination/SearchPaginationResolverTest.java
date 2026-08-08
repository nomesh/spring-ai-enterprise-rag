package com.nomesh.rag.search.pagination;

import com.nomesh.rag.search.SearchPagination;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SearchPaginationResolverTest {

    private final SearchPaginationResolver resolver =
            new SearchPaginationResolver();

    @Test
    void shouldUseDefaultsWhenPaginationIsNull() {

        SearchPagination resolved = resolver.resolve(null);

        assertEquals(0, resolved.page());
        assertEquals(20, resolved.size());
    }

    @Test
    void shouldUseDefaultPageWhenPageIsNull() {

        SearchPagination resolved =
                resolver.resolve(new SearchPagination(null, 10));

        assertEquals(0, resolved.page());
        assertEquals(10, resolved.size());
    }

    @Test
    void shouldUseDefaultSizeWhenSizeIsNull() {

        SearchPagination resolved =
                resolver.resolve(new SearchPagination(2, null));

        assertEquals(2, resolved.page());
        assertEquals(20, resolved.size());
    }

    @Test
    void shouldPreserveExplicitPagination() {

        SearchPagination resolved =
                resolver.resolve(new SearchPagination(3, 15));

        assertEquals(3, resolved.page());
        assertEquals(15, resolved.size());
    }
}