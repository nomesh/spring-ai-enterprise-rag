package com.nomesh.rag.controller;

import com.nomesh.rag.search.validation.EnterpriseSearchValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Converts application exceptions into consistent HTTP responses.
 *
 * <p>Keeping exception translation outside controllers prevents API concerns
 * from leaking into the search domain and retrieval logic.</p>
 *
 * @author Nomesh De Silva
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Converts search validation failures into HTTP 400 responses.
     *
     * @param exception validation failure
     * @return bad-request response containing the validation message
     */
    @ExceptionHandler(EnterpriseSearchValidationException.class)
    public ResponseEntity<Map<String, String>> handleSearchValidation(
            EnterpriseSearchValidationException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "INVALID_SEARCH_REQUEST",
                        "message", exception.getMessage()
                ));
    }
}