package com.nomesh.rag.search.mapper;

import com.nomesh.rag.search.SearchResult;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps Spring AI retrieval documents into framework-independent search results.
 *
 * <p>This mapper acts as an adapter between the infrastructure retrieval model
 * and the Enterprise RAG Platform's public search contract.</p>
 *
 * @author Nomesh De Silva
 */
@Component
public class SearchResultMapper {

    /**
     * Converts a Spring AI document into a platform search result.
     *
     * @param document retrieved Spring AI document
     * @return framework-independent search result
     */
    public SearchResult map(Document document) {
        return new SearchResult(
                document.getText(),
                document.getScore(),
                document.getMetadata()
        );
    }

    /**
     * Converts retrieved documents into platform search results.
     *
     * @param documents retrieved Spring AI documents
     * @return framework-independent search results
     */
    public List<SearchResult> map(List<Document> documents) {
        return documents.stream()
                .map(this::map)
                .toList();
    }
}