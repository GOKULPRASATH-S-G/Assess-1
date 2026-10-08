package com.petcare.service;

import com.petcare.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Queue;

/**
 * Service interface specifying veterinary appointment booking and lifecycle operations.
 * Demonstrates:
 * - Abstraction via Interface.
 * - Method Overloading (bookAppointment and cancelAppointment with defaults).
 * - Deliberate Queue contract for clinic patient triage and check-ins.
 */
public interface AppointmentService {
    // Overloaded booking methods
    Appointment bookAppointment(String petId, String ownerId, String veterinarianId, LocalDate appointmentDate, String reason);
    Appointment bookAppointment(String petId, String ownerId, String veterinarianId, LocalDate appointmentDate);

    Appointment completeAppointment(String appointmentId);

    // Overloaded cancellation methods
    Appointment cancelAppointment(String appointmentId, String reason);
    Appointment cancelAppointment(String appointmentId);

    List<Appointment> getAllAppointments();
    List<Appointment> getAppointmentsByVeterinarian(String veterinarianId);
    List<Appointment> getAppointmentsByOwner(String ownerId);
    List<Appointment> getAppointmentsByPet(String petId);
    Appointment getAppointmentById(String appointmentId);

    // Deliberate Queue contract for clinic check-in / triage queue
    Appointment checkInAppointment(String appointmentId);
    Queue<Appointment> getDailyQueue();
    Appointment processNextAppointmentInQueue();
}
