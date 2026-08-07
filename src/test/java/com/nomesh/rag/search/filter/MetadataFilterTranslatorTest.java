package com.nomesh.rag.search.filter;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MetadataFilterTranslatorTest {

    private final MetadataFilterTranslator translator =
            new MetadataFilterTranslator();

    @Test
    void shouldReturnEmptyWhenFilterIsNull() {

        Optional<Filter.Expression> result =
                translator.translate(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoFiltersAreProvided() {

        DocumentSearchFilter filter =
                new DocumentSearchFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Optional<Filter.Expression> result =
                translator.translate(filter);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCreateExpressionForSingleFilter() {

        DocumentSearchFilter filter =
                new DocumentSearchFilter(
                        "HR",
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Optional<Filter.Expression> result =
                translator.translate(filter);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldCreateExpressionForMultipleFilters() {

        DocumentSearchFilter filter =
                new DocumentSearchFilter(
                        "HR",
                        "pdf",
                        "LOCAL_UPLOAD",
                        null,
                        "INTERNAL",
                        null
                );

        Optional<Filter.Expression> result =
                translator.translate(filter);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldIgnoreBlankFilterValues() {

        DocumentSearchFilter filter =
                new DocumentSearchFilter(
                        " ",
                        "pdf",
                        null,
                        null,
                        null,
                        null
                );

        Optional<Filter.Expression> result =
                translator.translate(filter);

        assertTrue(result.isPresent());
    }
}