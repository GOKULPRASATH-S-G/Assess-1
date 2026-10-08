package com.petcare.service;

import com.petcare.exception.BusinessRuleException;
import com.petcare.exception.ValidationException;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Vaccination;
import com.petcare.model.Veterinarian;
import com.petcare.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In-memory implementation of MedicalService managing clinic visits, diagnoses, and vaccinations.
 */
public class MedicalServiceImpl implements MedicalService {
    private final Map<String, MedicalRecord> medicalRecords = new LinkedHashMap<>();
    private final Map<String, Vaccination> vaccinations = new LinkedHashMap<>();
    private final PetService petService;
    private final UserService userService;

    public MedicalServiceImpl(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
    }

    @Override
    public MedicalRecord addMedicalRecord(String petId, String veterinarianId, LocalDate visitDate,
                                          String diagnosis, String treatmentNotes) {
        if (petId == null || petId.trim().isEmpty()) {
            throw new ValidationException("Pet ID cannot be empty.");
        }
        if (veterinarianId == null || veterinarianId.trim().isEmpty()) {
            throw new ValidationException("Veterinarian ID cannot be empty.");
        }
        if (visitDate == null) {
            throw new ValidationException("Visit date cannot be null.");
        }
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new ValidationException("Diagnosis cannot be empty.");
        }
        if (treatmentNotes == null || treatmentNotes.trim().isEmpty()) {
            throw new ValidationException("Treatment/Notes cannot be empty.");
        }

        // Verify entities exist
        petService.getPetById(petId.trim());
        Veterinarian vet = userService.getVeterinarianById(veterinarianId.trim());

        String recordId = IdGenerator.nextId("MED");
        MedicalRecord record = new MedicalRecord(recordId, petId.trim(), vet.getId(),
                visitDate, diagnosis.trim(), treatmentNotes.trim());
        medicalRecords.put(recordId, record);
        return record;
    }

    @Override
    public Vaccination recordVaccination(String petId, String vaccineName,
                                         LocalDate vaccinationDate, LocalDate nextDueDate) {
        if (petId == null || petId.trim().isEmpty()) {
            throw new ValidationException("Pet ID cannot be empty.");
        }
        if (vaccineName == null || vaccineName.trim().isEmpty()) {
            throw new ValidationException("Vaccine name cannot be empty.");
        }
        if (vaccinationDate == null) {
            throw new ValidationException("Vaccination date cannot be null.");
        }
        if (nextDueDate == null) {
            throw new ValidationException("Next due date cannot be null.");
        }

        // Business Rule: Next vaccination date must be after vaccination date
        if (!nextDueDate.isAfter(vaccinationDate)) {
            throw new BusinessRuleException(
                    String.format("Invalid dates: Next vaccination due date (%s) must be strictly after the vaccination date (%s).",
                            nextDueDate, vaccinationDate));
        }

        // Verify pet exists
        petService.getPetById(petId.trim());

        String vaccinationId = IdGenerator.nextId("VAC");
        Vaccination vaccination = new Vaccination(vaccinationId, petId.trim(), vaccineName.trim(),
                vaccinationDate, nextDueDate);
        vaccinations.put(vaccinationId, vaccination);
        return vaccination;
    }

    @Override
    public Vaccination recordVaccinationWithAutoDueDate(String petId, String vaccineName,
                                                        LocalDate vaccinationDate, int boosterMonths) {
        int months = boosterMonths > 0 ? boosterMonths : 12; // default annual booster (12 months)
        LocalDate calculatedDueDate = vaccinationDate.plusMonths(months);
        return recordVaccination(petId, vaccineName, vaccinationDate, calculatedDueDate);
    }

    @Override
    public List<MedicalRecord> getMedicalHistoryByPet(String petId) {
        if (petId == null) return new ArrayList<>();
        // Verify pet exists
        petService.getPetById(petId.trim());
        return medicalRecords.values().stream()
                .filter(r -> petId.equalsIgnoreCase(r.getPetId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Vaccination> getVaccinationHistoryByPet(String petId) {
        if (petId == null) return new ArrayList<>();
        // Verify pet exists
        petService.getPetById(petId.trim());
        return vaccinations.values().stream()
                .filter(v -> petId.equalsIgnoreCase(v.getPetId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Vaccination> getAllVaccinations() {
        return new ArrayList<>(vaccinations.values());
    }

    @Override
    public List<Vaccination> getUpcomingVaccinations(LocalDate referenceDate) {
        LocalDate ref = referenceDate != null ? referenceDate : LocalDate.now();
        return vaccinations.values().stream()
                .filter(v -> v.getNextDueDate() != null && !v.getNextDueDate().isBefore(ref))
                .sorted(Comparator.comparing(Vaccination::getNextDueDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        return new ArrayList<>(medicalRecords.values());
    }

    @Override
    public Set<String> getUniqueVaccineTypes(String petId) {
        if (petId == null) {
            return new LinkedHashSet<>();
        }
        petService.getPetById(petId.trim());
        return vaccinations.values().stream()
                .filter(v -> petId.equalsIgnoreCase(v.getPetId()))
                .map(Vaccination::getVaccineName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
