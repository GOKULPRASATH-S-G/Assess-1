package com.petcare.service;

import com.petcare.model.AdoptionApplication;

import java.util.List;

/**
 * Service interface specifying adoption application operations and business rules.
 */
public interface AdoptionService {
    AdoptionApplication submitApplication(String petId, String adopterId, String reason);
    AdoptionApplication approveApplication(String applicationId, String reviewNotes);
    AdoptionApplication rejectApplication(String applicationId, String reviewNotes);
    List<AdoptionApplication> getAllApplications();
    List<AdoptionApplication> getPendingApplications();
    List<AdoptionApplication> getApplicationsByAdopter(String adopterId);
    List<AdoptionApplication> getApplicationsByPet(String petId);
    AdoptionApplication getApplicationById(String applicationId);
}
