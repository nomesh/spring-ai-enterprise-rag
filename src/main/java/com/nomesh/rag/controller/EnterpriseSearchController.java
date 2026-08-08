package com.nomesh.rag.controller;

import com.nomesh.rag.retrieval.DocumentRetriever;
import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.EnterpriseSearchResponse;
import com.nomesh.rag.search.SearchResult;
import com.nomesh.rag.search.mapper.SearchResultMapper;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Creates the enterprise search controller.
     *
     * @param documentRetriever retrieval component used to execute searches
     * @param searchResultMapper mapper used to convert infrastructure results
     */
    public EnterpriseSearchController(
            DocumentRetriever documentRetriever,
            SearchResultMapper searchResultMapper
    ) {
        this.documentRetriever = documentRetriever;
        this.searchResultMapper = searchResultMapper;
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

        List<Document> documents = documentRetriever.retrieve(request);
        List<SearchResult> results = searchResultMapper.map(documents);

        return new EnterpriseSearchResponse(
                request.query(),
                results.size(),
                results
        );
    }
}