package com.petcare.exception;

/**
 * Thrown when a requested entity (Pet, User, Application, Appointment, etc.) is not found.
 */
public class EntityNotFoundException extends PetCareException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
