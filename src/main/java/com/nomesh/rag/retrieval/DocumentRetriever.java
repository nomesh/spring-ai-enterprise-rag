package com.nomesh.rag.retrieval;

import com.nomesh.rag.search.filter.DocumentSearchFilter;
import com.nomesh.rag.search.filter.MetadataFilterTranslator;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Retrieves relevant documents from the vector store using semantic search.
 *
 * <p>Searches can optionally be narrowed using document metadata filters,
 * allowing the platform to combine semantic similarity with enterprise
 * attributes such as department, file type, or classification.</p>
 */
@Component
public class DocumentRetriever {

    private final VectorStore vectorStore;
    private final MetadataFilterTranslator filterTranslator;

    @Value("${rag.retrieval.top-k}")
    private int topK;

    @Value("${rag.retrieval.similarity-threshold}")
    private double similarityThreshold;

    /**
     * Creates a document retriever using the configured vector store
     * and metadata filter translator.
     *
     * @param vectorStore vector store used for similarity search
     * @param filterTranslator converts domain filters into Spring AI expressions
     */
    public DocumentRetriever(
            VectorStore vectorStore,
            MetadataFilterTranslator filterTranslator
    ) {
        this.vectorStore = vectorStore;
        this.filterTranslator = filterTranslator;
    }

    /**
     * Retrieves documents using semantic similarity only.
     *
     * @param question search query
     * @return relevant documents ordered by similarity
     */
    public List<Document> retrieve(String question) {
        return retrieve(question, null);
    }

    /**
     * Retrieves documents using semantic similarity and optional metadata filters.
     *
     * <p>When no metadata filters are provided, the search behaves exactly
     * like the existing semantic search flow.</p>
     *
     * @param question search query
     * @param filter optional document metadata filters
     * @return relevant documents matching both semantic and metadata criteria
     */
    public List<Document> retrieve(
            String question,
            DocumentSearchFilter filter
    ) {
        SearchRequest.Builder requestBuilder = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .similarityThreshold(similarityThreshold);

        Optional<Filter.Expression> filterExpression =
                filterTranslator.translate(filter);

        filterExpression.ifPresent(
                requestBuilder::filterExpression
        );

        return vectorStore.similaritySearch(
                requestBuilder.build()
        );
    }
}