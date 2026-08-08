package com.nomesh.rag.search.validation;

import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.SearchPagination;
import org.springframework.stereotype.Component;

/**
 * Validates enterprise search requests before retrieval execution.
 *
 * <p>Centralizing validation prevents search rules from being duplicated
 * across controllers, retrieval components, and future API adapters.</p>
 *
 * @author Nomesh De Silva
 */
@Component
public class EnterpriseSearchRequestValidator {

    private static final int MAX_TOP_K = 100;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Validates the supplied enterprise search request.
     *
     * @param request search request to validate
     * @throws EnterpriseSearchValidationException when the request is invalid
     */
    public void validate(EnterpriseSearchRequest request) {

        if (request == null) {
            throw new EnterpriseSearchValidationException(
                    "Search request must not be null."
            );
        }

        if (request.query() == null || request.query().isBlank()) {
            throw new EnterpriseSearchValidationException(
                    "Search query must not be blank."
            );
        }
        validatePagination(request.pagination());
        validateTopK(request.topK());
        validateSimilarityThreshold(request.similarityThreshold());
    }

    private void validatePagination(SearchPagination pagination) {
        if (pagination == null) {
            return;
        }

        if (pagination.page() != null && pagination.page() < 0) {
            throw new EnterpriseSearchValidationException(
                    "page must be zero or greater."
            );
        }

        if (pagination.size() != null && pagination.size() <= 0) {
            throw new EnterpriseSearchValidationException(
                    "size must be greater than zero."
            );
        }

        if (pagination.size() != null && pagination.size() > MAX_PAGE_SIZE) {
            throw new EnterpriseSearchValidationException(
                    "size must not exceed " + MAX_PAGE_SIZE + "."
            );
        }
    }

    private void validateTopK(Integer topK) {

        if (topK == null) {
            return;
        }

        if (topK <= 0) {
            throw new EnterpriseSearchValidationException(
                    "topK must be greater than zero."
            );
        }

        if (topK > MAX_TOP_K) {
            throw new EnterpriseSearchValidationException(
                    "topK must not exceed " + MAX_TOP_K + "."
            );
        }
    }

    private void validateSimilarityThreshold(Double similarityThreshold) {

        if (similarityThreshold == null) {
            return;
        }

        if (similarityThreshold < 0.0 || similarityThreshold > 1.0) {
            throw new EnterpriseSearchValidationException(
                    "similarityThreshold must be between 0.0 and 1.0."
            );
        }
    }
}