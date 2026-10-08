package com.petcare.service;

import com.petcare.model.Appointment;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface specifying veterinary appointment booking and lifecycle operations.
 */
public interface AppointmentService {
    Appointment bookAppointment(String petId, String ownerId, String veterinarianId, LocalDate appointmentDate, String reason);
    Appointment completeAppointment(String appointmentId);
    Appointment cancelAppointment(String appointmentId, String reason);
    List<Appointment> getAllAppointments();
    List<Appointment> getAppointmentsByVeterinarian(String veterinarianId);
    List<Appointment> getAppointmentsByOwner(String ownerId);
    List<Appointment> getAppointmentsByPet(String petId);
    Appointment getAppointmentById(String appointmentId);
}
