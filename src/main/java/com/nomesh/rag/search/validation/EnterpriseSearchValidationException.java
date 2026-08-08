package com.nomesh.rag.search.validation;

/**
 * Indicates that an enterprise search request violates platform validation rules.
 *
 * <p>The exception keeps search validation failures explicit and separate from
 * infrastructure errors so API clients can receive a clear bad-request response.</p>
 *
 * @author Nomesh De Silva
 */
public class EnterpriseSearchValidationException extends RuntimeException {

    /**
     * Creates a validation exception with the supplied message.
     *
     * @param message validation failure description
     */
    public EnterpriseSearchValidationException(String message) {
        super(message);
    }
}