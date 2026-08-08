package com.nomesh.rag.search.filter;

import com.nomesh.rag.metadata.MetadataConstants;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Converts document search filters into Spring AI metadata expressions.
 *
 * <p>This keeps search requests independent from the filtering syntax
 * required by the underlying vector store.</p>
 */
@Component
public class MetadataFilterTranslator {

    /**
     * Converts the provided search filters into a metadata filter expression.
     *
     * <p>Only filters with meaningful values are included. When no filters
     * are provided, an empty result is returned so normal semantic search
     * can continue without metadata restrictions.</p>
     *
     * @param filter document filters requested by the caller
     * @return translated filter expression when at least one filter exists
     */
    public Optional<Filter.Expression> translate(DocumentSearchFilter filter) {

        if (filter == null) {
            return Optional.empty();
        }

        FilterExpressionBuilder builder =
                new FilterExpressionBuilder();

        List<FilterExpressionBuilder.Op> expressions =
                new ArrayList<>();

        addIfPresent(
                expressions,
                builder,
                MetadataConstants.DEPARTMENT,
                filter.department()
        );

        addIfPresent(
                expressions,
                builder,
                MetadataConstants.FILE_TYPE,
                filter.fileType()
        );

        addIfPresent(
                expressions,
                builder,
                MetadataConstants.SOURCE,
                filter.source()
        );

        addIfPresent(
                expressions,
                builder,
                MetadataConstants.AUTHOR,
                filter.author()
        );

        addIfPresent(
                expressions,
                builder,
                MetadataConstants.CLASSIFICATION,
                filter.classification()
        );

        addIfPresent(
                expressions,
                builder,
                MetadataConstants.TENANT_ID,
                filter.tenantId()
        );

        if (expressions.isEmpty()) {
            return Optional.empty();
        }

        FilterExpressionBuilder.Op combined =
                expressions.get(0);

        for (int i = 1; i < expressions.size(); i++) {
            combined = builder.and(combined, expressions.get(i));
        }

        return Optional.of(combined.build());
    }

    /**
     * Adds an equality filter when the requested value is available.
     *
     * @param expressions filters being built
     * @param builder Spring AI filter builder
     * @param key metadata field name
     * @param value requested metadata value
     */
    private void addIfPresent(
            List<FilterExpressionBuilder.Op> expressions,
            FilterExpressionBuilder builder,
            String key,
            String value
    ) {
        if (value != null && !value.isBlank()) {
            expressions.add(builder.eq(key, value));
        }
    }
}