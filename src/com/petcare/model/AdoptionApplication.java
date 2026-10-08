package com.petcare.model;

import java.time.LocalDate;

/**
 * Represents an adoption application submitted by an adopter for a specific pet.
 */
public class AdoptionApplication {
    private final String applicationId;
    private final String petId;
    private final String adopterId;
    private String reason;
    private ApplicationStatus status;
    private final LocalDate applicationDate;
    private String reviewNotes;

    public AdoptionApplication(String applicationId, String petId, String adopterId, String reason) {
        this(applicationId, petId, adopterId, reason, ApplicationStatus.PENDING, LocalDate.now(), null);
    }

    public AdoptionApplication(String applicationId, String petId, String adopterId, String reason,
                               ApplicationStatus status, LocalDate applicationDate, String reviewNotes) {
        this.applicationId = applicationId;
        this.petId = petId;
        this.adopterId = adopterId;
        this.reason = reason;
        this.status = status;
        this.applicationDate = applicationDate;
        this.reviewNotes = reviewNotes;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getPetId() {
        return petId;
    }

    public String getAdopterId() {
        return adopterId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    @Override
    public String toString() {
        return String.format("[%s] Pet: %s | Adopter: %s | Date: %s | Status: %s | Reason: \"%s\"%s",
                applicationId, petId, adopterId, applicationDate, status, reason,
                (reviewNotes != null ? " | Notes: " + reviewNotes : ""));
    }
}
