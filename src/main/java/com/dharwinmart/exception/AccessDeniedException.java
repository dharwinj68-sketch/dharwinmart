package com.dharwinmart.exception;

/**
 * Exception thrown when an authenticated or unauthenticated user
 * attempts to access a restricted resource without proper permissions.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
