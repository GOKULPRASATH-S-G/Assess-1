package com.petcare.service;

import com.petcare.model.MedicalRecord;
import com.petcare.model.Vaccination;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface specifying medical examination and vaccination tracking operations.
 */
public interface MedicalService {
    MedicalRecord addMedicalRecord(String petId, String veterinarianId, LocalDate visitDate, String diagnosis, String treatmentNotes);
    Vaccination recordVaccination(String petId, String vaccineName, LocalDate vaccinationDate, LocalDate nextDueDate);
    Vaccination recordVaccinationWithAutoDueDate(String petId, String vaccineName, LocalDate vaccinationDate, int boosterMonths);
    List<MedicalRecord> getMedicalHistoryByPet(String petId);
    List<Vaccination> getVaccinationHistoryByPet(String petId);
    List<Vaccination> getAllVaccinations();
    List<Vaccination> getUpcomingVaccinations(LocalDate referenceDate);
    List<MedicalRecord> getAllMedicalRecords();
}
