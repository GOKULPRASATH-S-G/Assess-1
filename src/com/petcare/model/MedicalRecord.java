package com.petcare.model;

import java.time.LocalDate;

/**
 * Represents a medical examination visit record completed by a veterinarian.
 */
public class MedicalRecord {
    private final String recordId;
    private final String petId;
    private final String veterinarianId;
    private final LocalDate visitDate;
    private String diagnosis;
    private String treatmentNotes;

    public MedicalRecord(String recordId, String petId, String veterinarianId, LocalDate visitDate,
                         String diagnosis, String treatmentNotes) {
        this.recordId = recordId;
        this.petId = petId;
        this.veterinarianId = veterinarianId;
        this.visitDate = visitDate;
        this.diagnosis = diagnosis;
        this.treatmentNotes = treatmentNotes;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getPetId() {
        return petId;
    }

    public String getVeterinarianId() {
        return veterinarianId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentNotes() {
        return treatmentNotes;
    }

    public void setTreatmentNotes(String treatmentNotes) {
        this.treatmentNotes = treatmentNotes;
    }

    @Override
    public String toString() {
        return String.format("[%s] Date: %s | Pet: %s | Vet: %s | Diagnosis: %s | Treatment/Notes: %s",
                recordId, visitDate, petId, veterinarianId, diagnosis, treatmentNotes);
    }
}
