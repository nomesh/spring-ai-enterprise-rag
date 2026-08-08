package com.nomesh.rag.search.validation;

import com.nomesh.rag.search.EnterpriseSearchRequest;
import com.nomesh.rag.search.SearchPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnterpriseSearchRequestValidatorTest {

    private EnterpriseSearchRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EnterpriseSearchRequestValidator();
    }

    @Test
    void shouldAcceptValidRequest() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        10,
                        0.5,
                        null, null
                );

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldRejectNullRequest() {
        EnterpriseSearchValidationException exception =
                assertThrows(
                        EnterpriseSearchValidationException.class,
                        () -> validator.validate(null)
                );

        assertEquals(
                "Search request must not be null.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullQuery() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        null,
                        10,
                        0.5,
                        null, null
                );

        EnterpriseSearchValidationException exception =
                assertThrows(
                        EnterpriseSearchValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "Search query must not be blank.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankQuery() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "   ",
                        10,
                        0.5,
                        null, null
                );

        assertThrows(
                EnterpriseSearchValidationException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectZeroTopK() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        0,
                        0.5,
                        null, null
                );

        assertThrows(
                EnterpriseSearchValidationException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectTopKAboveMaximum() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        101,
                        0.5,
                        null, null
                );

        assertThrows(
                EnterpriseSearchValidationException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectSimilarityThresholdBelowZero() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        10,
                        -0.1,
                        null, null
                );

        assertThrows(
                EnterpriseSearchValidationException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectSimilarityThresholdAboveOne() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        10,
                        1.1,
                        null, null
                );

        assertThrows(
                EnterpriseSearchValidationException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldAcceptNullOptionalValues() {
        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        null,
                        null,
                        null, null
                );

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldRejectNegativePage() {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        10,
                        0.5,
                        null,
                        new SearchPagination(-1, 10)
                );

        EnterpriseSearchValidationException exception =
                assertThrows(
                        EnterpriseSearchValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "page must be zero or greater.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectZeroPageSize() {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        10,
                        0.5,
                        null,
                        new SearchPagination(0, 0)
                );

        EnterpriseSearchValidationException exception =
                assertThrows(
                        EnterpriseSearchValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "size must be greater than zero.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectPageSizeAboveMaximum() {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        10,
                        0.5,
                        null,
                        new SearchPagination(0, 101)
                );

        EnterpriseSearchValidationException exception =
                assertThrows(
                        EnterpriseSearchValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "size must not exceed 100.",
                exception.getMessage()
        );
    }

    @Test
    void shouldAcceptValidPagination() {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        20,
                        0.5,
                        null,
                        new SearchPagination(0, 10)
                );

        assertDoesNotThrow(
                () -> validator.validate(request)
        );
    }
}