package com.nomesh.rag.retrieval;

import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.filter.DocumentSearchFilter;
import com.nomesh.rag.search.filter.MetadataFilterTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class DocumentRetrieverTest {

    private VectorStore vectorStore;
    private MetadataFilterTranslator filterTranslator;
    private DocumentRetriever retriever;

    @BeforeEach
    void setUp() {
        vectorStore = mock(VectorStore.class);
        filterTranslator = mock(MetadataFilterTranslator.class);

        retriever = new DocumentRetriever(
                vectorStore,
                filterTranslator
        );

        // These values normally come from application.properties.
        ReflectionTestUtils.setField(retriever, "defaultTopK", 5);
        ReflectionTestUtils.setField(
                retriever,
                "defaultSimilarityThreshold",
                0.70
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());
    }

    @Test
    void shouldUsePlatformDefaultsForSemanticSearch() {

        when(filterTranslator.translate(null))
                .thenReturn(Optional.empty());

        retriever.retrieve("annual leave policy");

        ArgumentCaptor<SearchRequest> captor =
                ArgumentCaptor.forClass(SearchRequest.class);

        verify(vectorStore).similaritySearch(captor.capture());

        SearchRequest request = captor.getValue();

        assertEquals("annual leave policy", request.getQuery());
        assertEquals(5, request.getTopK());
        assertEquals(0.70, request.getSimilarityThreshold());
        assertNull(request.getFilterExpression());
    }

    @Test
    void shouldUseSearchRequestOverrides() {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave policy",
                        20,
                        0.85,
                        null, null
                );

        when(filterTranslator.translate(null))
                .thenReturn(Optional.empty());

        retriever.retrieve(request);

        ArgumentCaptor<SearchRequest> captor =
                ArgumentCaptor.forClass(SearchRequest.class);

        verify(vectorStore).similaritySearch(captor.capture());

        SearchRequest actualRequest = captor.getValue();

        assertEquals(20, actualRequest.getTopK());
        assertEquals(
                0.85,
                actualRequest.getSimilarityThreshold()
        );
    }

    @Test
    void shouldApplyMetadataFilterWhenProvided() {

        DocumentSearchFilter filter =
                new DocumentSearchFilter(
                        "HR",
                        "pdf",
                        null,
                        null,
                        null,
                        null
                );

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave policy",
                        null,
                        null,
                        filter, null
                );

        Filter.Expression expression = mock(Filter.Expression.class);

        when(filterTranslator.translate(filter))
                .thenReturn(Optional.of(expression));

        retriever.retrieve(request);

        ArgumentCaptor<SearchRequest> captor =
                ArgumentCaptor.forClass(SearchRequest.class);

        verify(vectorStore).similaritySearch(captor.capture());

        assertEquals(
                expression,
                captor.getValue().getFilterExpression()
        );
    }
}