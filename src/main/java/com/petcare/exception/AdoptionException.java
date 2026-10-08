package com.petcare.exception;

/**
 * Custom checked exception thrown when pet adoption business rules or lifecycle
 * constraints are violated (e.g. attempting to adopt an unavailable pet, duplicate
 * pending applications, or invalid application approvals/rejections).
 *
 * Demonstrates a custom Checked Exception requiring explicit throw/throws/catch handling.
 */
public class AdoptionException extends Exception {

    public AdoptionException(String message) {
        super(message);
    }

    public AdoptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
