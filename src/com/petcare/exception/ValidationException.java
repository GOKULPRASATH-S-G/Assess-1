package com.petcare.exception;

/**
 * Thrown when input validation fails (e.g. empty name, negative age, invalid date format).
 */
public class ValidationException extends PetCareException {
    public ValidationException(String message) {
        super(message);
    }
}
