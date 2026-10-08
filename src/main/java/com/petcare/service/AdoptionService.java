package com.petcare.service;

import com.petcare.exception.AdoptionException;
import com.petcare.model.AdoptionApplication;

import java.util.List;
import java.util.Queue;

/**
 * Service interface specifying adoption application operations and business rules.
 * Demonstrates:
 * - Interface-based architecture.
 * - Checked exception propagation (throws AdoptionException).
 * - Method overloading (approveApplication and rejectApplication with/without custom notes).
 * - Deliberate Queue contract for FIFO application review.
 */
public interface AdoptionService {
    AdoptionApplication submitApplication(String petId, String adopterId, String reason) throws AdoptionException;

    // Overloaded approval methods
    AdoptionApplication approveApplication(String applicationId, String reviewNotes) throws AdoptionException;
    AdoptionApplication approveApplication(String applicationId) throws AdoptionException;

    // Overloaded rejection methods
    AdoptionApplication rejectApplication(String applicationId, String reviewNotes) throws AdoptionException;
    AdoptionApplication rejectApplication(String applicationId) throws AdoptionException;

    List<AdoptionApplication> getAllApplications();
    List<AdoptionApplication> getPendingApplications();
    List<AdoptionApplication> getApplicationsByAdopter(String adopterId);
    List<AdoptionApplication> getApplicationsByPet(String petId);
    AdoptionApplication getApplicationById(String applicationId);

    // Deliberate Queue contract for FIFO application review
    Queue<AdoptionApplication> getApplicationQueue();
    AdoptionApplication processNextApplicationInQueue(boolean approve, String reviewNotes) throws AdoptionException;
}
