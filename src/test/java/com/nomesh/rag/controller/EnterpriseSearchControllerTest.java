package com.nomesh.rag.controller;

import com.nomesh.rag.retrieval.DocumentRetriever;
import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.EnterpriseSearchResponse;
import com.nomesh.rag.search.SearchPagination;
import com.nomesh.rag.search.SearchResult;
import com.nomesh.rag.search.mapper.SearchResultMapper;
import com.nomesh.rag.search.pagination.SearchPaginationResolver;
import com.nomesh.rag.search.pagination.SearchWindowResolver;
import com.nomesh.rag.search.validation.EnterpriseSearchRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnterpriseSearchControllerTest {

    private DocumentRetriever documentRetriever;
    private SearchResultMapper searchResultMapper;
    private EnterpriseSearchRequestValidator requestValidator;

    private EnterpriseSearchController controller;

    @BeforeEach
    void setUp() {

        documentRetriever = mock(DocumentRetriever.class);
        searchResultMapper = mock(SearchResultMapper.class);
        requestValidator = mock(EnterpriseSearchRequestValidator.class);

        controller = new EnterpriseSearchController(
                documentRetriever,
                searchResultMapper,
                requestValidator,
                new SearchPaginationResolver(),
                new SearchWindowResolver()
        );
    }

    @Test
    void shouldReturnFirstPageAndIndicateMoreResults() {

        EnterpriseSearchRequest request =
                request(new SearchPagination(0, 2));

        List<Document> documents =
                List.of(
                        mock(Document.class),
                        mock(Document.class),
                        mock(Document.class)
                );

        List<SearchResult> candidates =
                List.of(
                        result("result-1"),
                        result("result-2"),
                        result("result-3")
                );

        when(documentRetriever.retrieve(request, 3))
                .thenReturn(documents);

        when(searchResultMapper.map(documents))
                .thenReturn(candidates);

        EnterpriseSearchResponse response =
                controller.search(request);

        assertEquals(2, response.results().size());
        assertEquals("result-1", response.results().get(0).content());
        assertEquals("result-2", response.results().get(1).content());

        assertEquals(0, response.pagination().page());
        assertEquals(2, response.pagination().size());
        assertEquals(2, response.pagination().returnedResults());
        assertTrue(response.pagination().hasMore());

        verify(requestValidator).validate(request);
        verify(documentRetriever).retrieve(request, 3);
    }

    @Test
    void shouldReturnSecondPage() {

        EnterpriseSearchRequest request =
                request(new SearchPagination(1, 2));

        List<Document> documents =
                List.of(
                        mock(Document.class),
                        mock(Document.class),
                        mock(Document.class),
                        mock(Document.class),
                        mock(Document.class)
                );

        List<SearchResult> candidates =
                List.of(
                        result("result-1"),
                        result("result-2"),
                        result("result-3"),
                        result("result-4"),
                        result("result-5")
                );

        when(documentRetriever.retrieve(request, 5))
                .thenReturn(documents);

        when(searchResultMapper.map(documents))
                .thenReturn(candidates);

        EnterpriseSearchResponse response =
                controller.search(request);

        assertEquals(2, response.results().size());
        assertEquals("result-3", response.results().get(0).content());
        assertEquals("result-4", response.results().get(1).content());

        assertEquals(1, response.pagination().page());
        assertTrue(response.pagination().hasMore());

        verify(documentRetriever).retrieve(request, 5);
    }

    @Test
    void shouldIndicateNoMoreResultsWhenExtraCandidateDoesNotExist() {

        EnterpriseSearchRequest request =
                request(new SearchPagination(0, 2));

        List<Document> documents =
                List.of(
                        mock(Document.class),
                        mock(Document.class)
                );

        List<SearchResult> candidates =
                List.of(
                        result("result-1"),
                        result("result-2")
                );

        when(documentRetriever.retrieve(request, 3))
                .thenReturn(documents);

        when(searchResultMapper.map(documents))
                .thenReturn(candidates);

        EnterpriseSearchResponse response =
                controller.search(request);

        assertEquals(2, response.results().size());
        assertFalse(response.pagination().hasMore());
    }

    @Test
    void shouldReturnEmptyPageWhenRequestedOffsetExceedsCandidates() {

        EnterpriseSearchRequest request =
                request(new SearchPagination(1, 2));

        List<Document> documents =
                List.of(
                        mock(Document.class)
                );

        List<SearchResult> candidates =
                List.of(
                        result("result-1")
                );

        when(documentRetriever.retrieve(request, 5))
                .thenReturn(documents);

        when(searchResultMapper.map(documents))
                .thenReturn(candidates);

        EnterpriseSearchResponse response =
                controller.search(request);

        assertTrue(response.results().isEmpty());
        assertEquals(0, response.pagination().returnedResults());
        assertFalse(response.pagination().hasMore());
    }

    @Test
    void shouldApplyDefaultPaginationWhenNotProvided() {

        EnterpriseSearchRequest request =
                request(null);

        when(documentRetriever.retrieve(eq(request), eq(21)))
                .thenReturn(List.of());

        when(searchResultMapper.map(Collections.singletonList(any())))
                .thenReturn(List.of());

        EnterpriseSearchResponse response =
                controller.search(request);

        assertEquals(0, response.pagination().page());
        assertEquals(20, response.pagination().size());
        assertEquals(0, response.pagination().returnedResults());
        assertFalse(response.pagination().hasMore());

        verify(documentRetriever).retrieve(request, 21);
    }

    private EnterpriseSearchRequest request(
            SearchPagination pagination
    ) {

        return new EnterpriseSearchRequest(
                "annual leave",
                null,
                0.5,
                null,
                pagination
        );
    }

    private SearchResult result(String content) {

        return new SearchResult(
                content,
                0.9,
                Map.of()
        );
    }
}