package com.petcare.service;

import com.petcare.exception.BusinessRuleException;
import com.petcare.exception.EntityNotFoundException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Adopter;
import com.petcare.model.Appointment;
import com.petcare.model.AppointmentStatus;
import com.petcare.model.Pet;
import com.petcare.model.Veterinarian;
import com.petcare.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * In-memory implementation of AppointmentService enforcing appointment business rules.
 * Demonstrates Map storage, List filtering, and deliberate Queue usage for clinic patient check-ins.
 */
public class AppointmentServiceImpl implements AppointmentService {
    private final Map<String, Appointment> appointments = new LinkedHashMap<>();
    private final Queue<Appointment> checkInQueue = new LinkedList<>();
    private final PetService petService;
    private final UserService userService;

    public AppointmentServiceImpl(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
    }

    @Override
    public Appointment bookAppointment(String petId, String ownerId, String veterinarianId,
                                       LocalDate appointmentDate, String reason) {
        if (petId == null || petId.trim().isEmpty()) {
            throw new ValidationException("Pet ID cannot be empty.");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new ValidationException("Owner ID cannot be empty.");
        }
        if (veterinarianId == null || veterinarianId.trim().isEmpty()) {
            throw new ValidationException("Veterinarian ID cannot be empty.");
        }
        if (appointmentDate == null) {
            throw new ValidationException("Appointment date cannot be null.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Reason for appointment cannot be empty.");
        }

        // Validate entities
        Pet pet = petService.getPetById(petId.trim());
        Adopter owner = userService.getAdopterById(ownerId.trim());
        Veterinarian vet = userService.getVeterinarianById(veterinarianId.trim());

        // Business Rule: Only adopted/owned pets can be booked for owner veterinary appointments
        if (!pet.isAdopted() || !owner.ownsPet(pet.getPetId())) {
            throw new BusinessRuleException(
                    String.format("Cannot book appointment: Pet %s (%s) is not an adopted pet owned by %s.",
                            pet.getPetId(), pet.getName(), owner.getName()));
        }

        String appointmentId = IdGenerator.nextId("APT");
        Appointment appointment = new Appointment(appointmentId, pet.getPetId(), owner.getId(),
                vet.getId(), appointmentDate, reason.trim());
        appointments.put(appointmentId, appointment);
        return appointment;
    }

    @Override
    public Appointment completeAppointment(String appointmentId) {
        Appointment apt = getAppointmentById(appointmentId);

        // Business Rule: A completed appointment cannot be completed again
        if (apt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Appointment " + appointmentId + " is already marked as COMPLETED.");
        }
        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot complete a cancelled appointment (" + appointmentId + ").");
        }

        apt.setStatus(AppointmentStatus.COMPLETED);
        checkInQueue.remove(apt);
        return apt;
    }

    @Override
    public Appointment cancelAppointment(String appointmentId, String reason) {
        Appointment apt = getAppointmentById(appointmentId);

        if (apt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot cancel an appointment that has already been completed.");
        }
        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Appointment " + appointmentId + " is already cancelled.");
        }

        apt.setStatus(AppointmentStatus.CANCELLED);
        checkInQueue.remove(apt);
        return apt;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    @Override
    public List<Appointment> getAppointmentsByVeterinarian(String veterinarianId) {
        if (veterinarianId == null) return new ArrayList<>();
        return appointments.values().stream()
                .filter(a -> veterinarianId.equalsIgnoreCase(a.getVeterinarianId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> getAppointmentsByOwner(String ownerId) {
        if (ownerId == null) return new ArrayList<>();
        return appointments.values().stream()
                .filter(a -> ownerId.equalsIgnoreCase(a.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> getAppointmentsByPet(String petId) {
        if (petId == null) return new ArrayList<>();
        return appointments.values().stream()
                .filter(a -> petId.equalsIgnoreCase(a.getPetId()))
                .collect(Collectors.toList());
    }

    @Override
    public Appointment getAppointmentById(String appointmentId) {
        if (appointmentId == null || !appointments.containsKey(appointmentId)) {
            throw new EntityNotFoundException("Appointment '" + appointmentId + "' not found.");
        }
        return appointments.get(appointmentId);
    }

    @Override
    public Appointment checkInAppointment(String appointmentId) {
        Appointment apt = getAppointmentById(appointmentId);
        if (apt.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessRuleException("Only BOOKED appointments can be checked in to the daily clinic queue.");
        }
        if (!checkInQueue.contains(apt)) {
            checkInQueue.offer(apt);
        }
        return apt;
    }

    @Override
    public Queue<Appointment> getDailyQueue() {
        return new LinkedList<>(checkInQueue);
    }

    @Override
    public Appointment processNextAppointmentInQueue() {
        if (checkInQueue.isEmpty()) {
            throw new BusinessRuleException("No checked-in patients currently in the clinic queue.");
        }
        Appointment apt = checkInQueue.poll();
        apt.setStatus(AppointmentStatus.COMPLETED);
        return apt;
    }
}
