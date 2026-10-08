package com.petcare.exception;

/**
 * Base custom unchecked exception for the Pet Care application.
 */
public class PetCareException extends RuntimeException {
    public PetCareException(String message) {
        super(message);
    }

    public PetCareException(String message, Throwable cause) {
        super(message, cause);
    }
}
