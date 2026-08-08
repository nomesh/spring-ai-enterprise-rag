package com.nomesh.rag.controller;

import com.nomesh.rag.retrieval.DocumentRetriever;
import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.EnterpriseSearchResponse;
import com.nomesh.rag.search.SearchPageMetadata;
import com.nomesh.rag.search.SearchResult;
import com.nomesh.rag.search.mapper.SearchResultMapper;
import com.nomesh.rag.search.validation.EnterpriseSearchRequestValidator;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import com.nomesh.rag.search.SearchPagination;
import com.nomesh.rag.search.pagination.SearchPaginationResolver;
import com.nomesh.rag.search.pagination.SearchWindow;
import com.nomesh.rag.search.pagination.SearchWindowResolver;

import java.util.List;

/**
 * Exposes enterprise semantic search capabilities.
 *
 * <p>The controller accepts platform-level search requests and returns
 * framework-independent search responses, preventing Spring AI implementation
 * details from leaking into the public API contract.</p>
 *
 * @author Nomesh De Silva
 */
@RestController
@RequestMapping("/api/v1/search")
public class EnterpriseSearchController {

    private final DocumentRetriever documentRetriever;
    private final SearchResultMapper searchResultMapper;
    private final EnterpriseSearchRequestValidator requestValidator;

    private final SearchPaginationResolver paginationResolver;
    private final SearchWindowResolver searchWindowResolver;

    /**
     * Creates the enterprise search controller.
     *
     * @param documentRetriever retrieval component used to execute searches
     * @param searchResultMapper mapper used to convert infrastructure results
     * @param requestValidator validator used to enforce search request rules
     * @param paginationResolver resolver used to apply pagination defaults
     * @param searchWindowResolver resolver used to calculate bounded retrieval windows
     */
    public EnterpriseSearchController(
            DocumentRetriever documentRetriever,
            SearchResultMapper searchResultMapper,
            EnterpriseSearchRequestValidator requestValidator,
            SearchPaginationResolver paginationResolver,
            SearchWindowResolver searchWindowResolver
    ) {
        this.documentRetriever = documentRetriever;
        this.searchResultMapper = searchResultMapper;
        this.requestValidator = requestValidator;
        this.paginationResolver = paginationResolver;
        this.searchWindowResolver = searchWindowResolver;
    }

    /**
     * Executes semantic search with optional enterprise metadata filters.
     *
     * @param request enterprise search criteria
     * @return enterprise search response
     */
    @PostMapping
    public EnterpriseSearchResponse search(
            @RequestBody EnterpriseSearchRequest request
    ) {

        requestValidator.validate(request);

        SearchPagination pagination =
                paginationResolver.resolve(request.pagination());

        SearchWindow window =
                searchWindowResolver.resolve(pagination);

        List<Document> documents =
                documentRetriever.retrieve(
                        request,
                        window.retrievalLimit()
                );

        List<SearchResult> candidates =
                searchResultMapper.map(documents);

        int fromIndex = Math.min(
                window.offset(),
                candidates.size()
        );

        int toIndex = Math.min(
                fromIndex + window.pageSize(),
                candidates.size()
        );

        List<SearchResult> pageResults =
                List.copyOf(
                        candidates.subList(fromIndex, toIndex)
                );

        boolean hasMore =
                candidates.size() > toIndex;

        return new EnterpriseSearchResponse(
                request.query(),
                pageResults,
                new SearchPageMetadata(
                        pagination.page(),
                        pagination.size(),
                        pageResults.size(),
                        hasMore
                )
        );
    }
}