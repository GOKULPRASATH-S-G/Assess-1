package com.petcare.service;

import com.petcare.exception.AdoptionException;
import com.petcare.exception.EntityNotFoundException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Adopter;
import com.petcare.model.AdoptionApplication;
import com.petcare.model.ApplicationStatus;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;
import com.petcare.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Implementation of AdoptionService enforcing pet adoption business rules.
 * Demonstrates:
 * - Checked Exception handling (AdoptionException).
 * - Collection choices:
 *     * Map (LinkedHashMap): O(1) key lookups by applicationId while preserving insertion order.
 *     * Queue (LinkedList): FIFO processing of pending applications without starvation.
 *     * List (ArrayList): Returning ordered copies of records for presentation.
 * - Method Overloading on approveApplication and rejectApplication.
 * - Constants for fixed business rules and default notes.
 */
public class AdoptionServiceImpl implements AdoptionService {

    // Constants for fixed business rules
    public static final String APPLICATION_ID_PREFIX = "APP";
    public static final String DEFAULT_APPROVAL_NOTES = "Approved by shelter staff";
    public static final String DEFAULT_REJECTION_NOTES = "Rejected by shelter staff";

    // Collections
    private final Map<String, AdoptionApplication> applications = new LinkedHashMap<>();
    private final Queue<AdoptionApplication> applicationQueue = new LinkedList<>();

    private final PetService petService;
    private final UserService userService;

    public AdoptionServiceImpl(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
    }

    @Override
    public AdoptionApplication submitApplication(String petId, String adopterId, String reason) throws AdoptionException {
        if (petId == null || petId.trim().isEmpty()) {
            throw new ValidationException("Pet ID cannot be empty.");
        }
        if (adopterId == null || adopterId.trim().isEmpty()) {
            throw new ValidationException("Adopter ID cannot be empty.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Reason for adoption cannot be empty.");
        }

        // Validate adopter existence and type
        Adopter adopter = userService.getAdopterById(adopterId.trim());

        // Validate pet existence
        Pet pet = petService.getPetById(petId.trim());

        // Business Rule: Prevent applications for pets that are already adopted
        if (!pet.isAvailable()) {
            throw new AdoptionException(
                    String.format("Cannot submit application: Pet %s (%s) is already %s.",
                            pet.getPetId(), pet.getName(), pet.getAdoptionStatus()));
        }

        // Business Rule: Cannot submit multiple active applications for the same pet by the same adopter
        boolean hasActiveApplication = applications.values().stream()
                .anyMatch(app -> app.getPetId().equalsIgnoreCase(petId.trim())
                        && app.getAdopterId().equalsIgnoreCase(adopterId.trim())
                        && app.isPending());

        if (hasActiveApplication) {
            throw new AdoptionException(
                    String.format("Adopter %s already has an active pending application for Pet %s.",
                            adopterId, petId));
        }

        String applicationId = IdGenerator.nextId(APPLICATION_ID_PREFIX);
        AdoptionApplication app = new AdoptionApplication(applicationId, pet.getPetId(), adopter.getId(), reason.trim());
        applications.put(applicationId, app);
        applicationQueue.offer(app); // Enqueue for FIFO triage
        return app;
    }

    // Overload 1: full specification with review notes
    @Override
    public AdoptionApplication approveApplication(String applicationId, String reviewNotes) throws AdoptionException {
        AdoptionApplication app = getApplicationById(applicationId);

        // Business Rule: Cannot approve an already approved or rejected application
        if (app.getStatus() == ApplicationStatus.APPROVED) {
            throw new AdoptionException("Application " + applicationId + " is already approved.");
        }
        if (app.getStatus() == ApplicationStatus.REJECTED) {
            throw new AdoptionException("Cannot approve an already rejected application.");
        }

        Pet pet = petService.getPetById(app.getPetId());

        // Business Rule: Cannot approve an application if the pet has already been adopted
        if (pet.isAdopted()) {
            throw new AdoptionException(
                    String.format("Cannot approve application: Pet %s (%s) has already been adopted by another applicant (%s).",
                            pet.getPetId(), pet.getName(), pet.getOwnerId()));
        }

        // State changes
        app.setStatus(ApplicationStatus.APPROVED);
        app.setReviewNotes(reviewNotes != null && !reviewNotes.trim().isEmpty() ? reviewNotes.trim() : DEFAULT_APPROVAL_NOTES);

        // Update Pet
        petService.updatePetStatus(pet.getPetId(), PetStatus.ADOPTED, app.getAdopterId());

        // Update Adopter's record
        Adopter adopter = userService.getAdopterById(app.getAdopterId());
        adopter.addAdoptedPet(pet.getPetId());

        // Auto-reject other pending applications for the same pet
        for (AdoptionApplication otherApp : applications.values()) {
            if (!otherApp.getApplicationId().equals(app.getApplicationId())
                    && otherApp.getPetId().equals(pet.getPetId())
                    && otherApp.isPending()) {
                otherApp.setStatus(ApplicationStatus.REJECTED);
                otherApp.setReviewNotes("Automatically rejected: Pet adopted by applicant " + app.getAdopterId());
            }
        }

        // Synchronize review queue
        applicationQueue.removeIf(a -> !a.isPending());

        return app;
    }

    // Overload 2: defaults to standard approval notes
    @Override
    public AdoptionApplication approveApplication(String applicationId) throws AdoptionException {
        return approveApplication(applicationId, DEFAULT_APPROVAL_NOTES);
    }

    // Overload 1: full specification with rejection reason
    @Override
    public AdoptionApplication rejectApplication(String applicationId, String reviewNotes) throws AdoptionException {
        AdoptionApplication app = getApplicationById(applicationId);

        if (app.getStatus() == ApplicationStatus.APPROVED) {
            throw new AdoptionException("Cannot reject an application that is already approved.");
        }
        if (app.getStatus() == ApplicationStatus.REJECTED) {
            throw new AdoptionException("Application " + applicationId + " is already rejected.");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setReviewNotes(reviewNotes != null && !reviewNotes.trim().isEmpty() ? reviewNotes.trim() : DEFAULT_REJECTION_NOTES);

        // Synchronize review queue
        applicationQueue.removeIf(a -> !a.isPending());

        return app;
    }

    // Overload 2: defaults to standard rejection notes
    @Override
    public AdoptionApplication rejectApplication(String applicationId) throws AdoptionException {
        return rejectApplication(applicationId, DEFAULT_REJECTION_NOTES);
    }

    @Override
    public List<AdoptionApplication> getAllApplications() {
        return new ArrayList<>(applications.values());
    }

    @Override
    public List<AdoptionApplication> getPendingApplications() {
        return applications.values().stream()
                .filter(AdoptionApplication::isPending)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdoptionApplication> getApplicationsByAdopter(String adopterId) {
        if (adopterId == null) return new ArrayList<>();
        return applications.values().stream()
                .filter(a -> adopterId.equalsIgnoreCase(a.getAdopterId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AdoptionApplication> getApplicationsByPet(String petId) {
        if (petId == null) return new ArrayList<>();
        return applications.values().stream()
                .filter(a -> petId.equalsIgnoreCase(a.getPetId()))
                .collect(Collectors.toList());
    }

    @Override
    public AdoptionApplication getApplicationById(String applicationId) {
        if (applicationId == null || !applications.containsKey(applicationId)) {
            throw new EntityNotFoundException("Adoption application '" + applicationId + "' not found.");
        }
        return applications.get(applicationId);
    }

    @Override
    public Queue<AdoptionApplication> getApplicationQueue() {
        return new LinkedList<>(applicationQueue);
    }

    @Override
    public AdoptionApplication processNextApplicationInQueue(boolean approve, String reviewNotes) throws AdoptionException {
        while (!applicationQueue.isEmpty()) {
            AdoptionApplication candidate = applicationQueue.poll();
            if (candidate.isPending()) {
                if (approve) {
                    return approveApplication(candidate.getApplicationId(), reviewNotes);
                } else {
                    return rejectApplication(candidate.getApplicationId(), reviewNotes);
                }
            }
        }
        throw new AdoptionException("No pending applications currently in queue.");
    }
}
