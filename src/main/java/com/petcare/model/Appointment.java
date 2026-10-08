package com.petcare.model;

import java.time.LocalDate;

/**
 * Represents a veterinary appointment booked by a pet owner.
 */
public class Appointment {
    private final String appointmentId;
    private final String petId;
    private final String ownerId;
    private final String veterinarianId;
    private LocalDate appointmentDate;
    private String reason;
    private AppointmentStatus status;

    public Appointment(String appointmentId, String petId, String ownerId, String veterinarianId,
                       LocalDate appointmentDate, String reason) {
        this(appointmentId, petId, ownerId, veterinarianId, appointmentDate, reason, AppointmentStatus.BOOKED);
    }

    public Appointment(String appointmentId, String petId, String ownerId, String veterinarianId,
                       LocalDate appointmentDate, String reason, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.petId = petId;
        this.ownerId = ownerId;
        this.veterinarianId = veterinarianId;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPetId() {
        return petId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getVeterinarianId() {
        return veterinarianId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public boolean isBooked() {
        return this.status == AppointmentStatus.BOOKED;
    }

    public boolean isCompleted() {
        return this.status == AppointmentStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return String.format("[%s] Date: %s | Pet: %s | Owner: %s | Vet: %s | Status: %s | Reason: \"%s\"",
                appointmentId, appointmentDate, petId, ownerId, veterinarianId, status, reason);
    }
}
