package com.petcare.exception;

/**
 * Thrown when a business logic invariant is violated (e.g. adopting an already adopted pet,
 * approving a rejected application, completing an already completed appointment).
 */
public class BusinessRuleException extends PetCareException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
