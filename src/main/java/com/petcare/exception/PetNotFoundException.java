package com.petcare.exception;

/**
 * Custom unchecked exception thrown when a requested Pet entity cannot be found.
 * Inherits from EntityNotFoundException (which extends PetCareException / RuntimeException).
 *
 * Demonstrates a domain-specific custom Unchecked Exception.
 */
public class PetNotFoundException extends EntityNotFoundException {

    public PetNotFoundException(String message) {
        super(message);
    }
}
