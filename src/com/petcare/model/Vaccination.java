package com.petcare.model;

import java.time.LocalDate;

/**
 * Represents a vaccination administered to a pet, including future booster due date.
 */
public class Vaccination {
    private final String vaccinationId;
    private final String petId;
    private String vaccineName;
    private LocalDate vaccinationDate;
    private LocalDate nextDueDate;

    public Vaccination(String vaccinationId, String petId, String vaccineName,
                       LocalDate vaccinationDate, LocalDate nextDueDate) {
        this.vaccinationId = vaccinationId;
        this.petId = petId;
        this.vaccineName = vaccineName;
        this.vaccinationDate = vaccinationDate;
        this.nextDueDate = nextDueDate;
    }

    public String getVaccinationId() {
        return vaccinationId;
    }

    public String getPetId() {
        return petId;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(LocalDate vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public boolean isUpcoming(LocalDate referenceDate) {
        return nextDueDate != null && (nextDueDate.isEqual(referenceDate) || nextDueDate.isAfter(referenceDate));
    }

    @Override
    public String toString() {
        return String.format("[%s] Pet: %s | Vaccine: %s | Administered: %s | Next Due: %s",
                vaccinationId, petId, vaccineName, vaccinationDate, nextDueDate);
    }
}
