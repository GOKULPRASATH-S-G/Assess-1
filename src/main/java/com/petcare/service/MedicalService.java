package com.petcare.service;

import com.petcare.model.MedicalRecord;
import com.petcare.model.Vaccination;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Service interface specifying medical examination and vaccination tracking operations.
 * Demonstrates Abstraction via Interface, Method Overloading, and deliberate Set collection queries.
 */
public interface MedicalService {
    MedicalRecord addMedicalRecord(String petId, String veterinarianId, LocalDate visitDate, String diagnosis, String treatmentNotes);

    // Method Overload 1: full specification with explicit next due date
    Vaccination recordVaccination(String petId, String vaccineName, LocalDate vaccinationDate, LocalDate nextDueDate);

    // Method Overload 2: defaults to standard annual (12-month) booster
    Vaccination recordVaccination(String petId, String vaccineName, LocalDate vaccinationDate);

    // Method Overload 3: specifies custom booster interval in months
    Vaccination recordVaccination(String petId, String vaccineName, LocalDate vaccinationDate, int boosterMonths);

    // Backward-compatible alias
    Vaccination recordVaccinationWithAutoDueDate(String petId, String vaccineName, LocalDate vaccinationDate, int boosterMonths);

    List<MedicalRecord> getMedicalHistoryByPet(String petId);
    List<Vaccination> getVaccinationHistoryByPet(String petId);
    List<Vaccination> getAllVaccinations();
    List<Vaccination> getUpcomingVaccinations(LocalDate referenceDate);
    List<MedicalRecord> getAllMedicalRecords();

    // Deliberate Set operation for distinct administered vaccines
    Set<String> getUniqueVaccineTypes(String petId);
}
