package com.nomesh.rag.search.pagination;

import com.nomesh.rag.search.SearchPagination;
import com.nomesh.rag.search.validation.EnterpriseSearchValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchWindowResolverTest {

    private final SearchWindowResolver resolver =
            new SearchWindowResolver();

    @Test
    void shouldResolveFirstPageWindow() {

        SearchWindow window =
                resolver.resolve(new SearchPagination(0, 10));

        assertEquals(0, window.offset());
        assertEquals(10, window.pageSize());
        assertEquals(11, window.retrievalLimit());
    }

    @Test
    void shouldResolveSecondPageWindow() {

        SearchWindow window =
                resolver.resolve(new SearchPagination(1, 10));

        assertEquals(10, window.offset());
        assertEquals(10, window.pageSize());
        assertEquals(21, window.retrievalLimit());
    }

    @Test
    void shouldResolveLaterPageWindow() {

        SearchWindow window =
                resolver.resolve(new SearchPagination(2, 20));

        assertEquals(40, window.offset());
        assertEquals(20, window.pageSize());
        assertEquals(61, window.retrievalLimit());
    }

    @Test
    void shouldRejectWindowAboveMaximumRetrievalLimit() {

        EnterpriseSearchValidationException exception =
                assertThrows(
                        EnterpriseSearchValidationException.class,
                        () -> resolver.resolve(
                                new SearchPagination(5, 20)
                        )
                );

        assertEquals(
                "Requested pagination window exceeds the maximum retrieval limit of 100.",
                exception.getMessage()
        );
    }
}