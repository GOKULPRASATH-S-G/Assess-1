package com.petcare;

import com.petcare.exception.BusinessRuleException;
import com.petcare.model.Adopter;
import com.petcare.model.Appointment;
import com.petcare.model.AppointmentStatus;
import com.petcare.model.Gender;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;
import com.petcare.model.Vaccination;
import com.petcare.model.Veterinarian;
import com.petcare.service.AppointmentService;
import com.petcare.service.AppointmentServiceImpl;
import com.petcare.service.MedicalService;
import com.petcare.service.MedicalServiceImpl;
import com.petcare.service.PetService;
import com.petcare.service.PetServiceImpl;
import com.petcare.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flow 2: Veterinary Appointment & Vaccination Tests")
public class VeterinaryFlowTest {

    private PetService petService;
    private UserService userService;
    private AppointmentService appointmentService;
    private MedicalService medicalService;
    private Adopter owner;
    private Veterinarian vet;
    private Pet pet;

    @BeforeEach
    void setUp() {
        petService = new PetServiceImpl();
        userService = new UserService();
        appointmentService = new AppointmentServiceImpl(petService, userService);
        medicalService = new MedicalServiceImpl(petService, userService);

        owner = new Adopter("A201", "Charlie Brown", "charlie@example.com", "555-3333");
        vet = new Veterinarian("V201", "Dr. Emily Green", "emily@vetclinic.com", "555-4444", "Surgery", "LIC-777");
        userService.registerUser(owner);
        userService.registerUser(vet);

        pet = petService.addPet("Snoopy", "Dog", "Beagle", 3, Gender.MALE);
        // Simulate adopted pet ownership
        petService.updatePetStatus(pet.getPetId(), PetStatus.ADOPTED, owner.getId());
        owner.addAdoptedPet(pet.getPetId());
    }

    @Test
    @DisplayName("Book and complete appointment for adopted pet")
    void testBookAndCompleteAppointmentSuccess() {
        Appointment apt = appointmentService.bookAppointment(pet.getPetId(), owner.getId(),
                vet.getId(), LocalDate.now().plusDays(2), "Annual physical exam");
        assertNotNull(apt);
        assertEquals(AppointmentStatus.BOOKED, apt.getStatus());

        Appointment completed = appointmentService.completeAppointment(apt.getAppointmentId());
        assertEquals(AppointmentStatus.COMPLETED, completed.getStatus());
    }

    @Test
    @DisplayName("Verify clinic check-in queue (FIFO)")
    void testAppointmentCheckInQueue() {
        Appointment apt = appointmentService.bookAppointment(pet.getPetId(), owner.getId(),
                vet.getId(), LocalDate.now(), "Vaccination check");
        appointmentService.checkInAppointment(apt.getAppointmentId());

        Queue<Appointment> queue = appointmentService.getDailyQueue();
        assertFalse(queue.isEmpty());
        assertEquals(apt.getAppointmentId(), queue.peek().getAppointmentId());

        Appointment processed = appointmentService.processNextAppointmentInQueue();
        assertEquals(apt.getAppointmentId(), processed.getAppointmentId());
        assertEquals(AppointmentStatus.COMPLETED, processed.getStatus());
    }

    @Test
    @DisplayName("Add medical record and record vaccination with auto-calculated due date")
    void testMedicalRecordAndVaccinationFlow() {
        MedicalRecord record = medicalService.addMedicalRecord(pet.getPetId(), vet.getId(),
                LocalDate.now(), "Healthy", "Vaccination administered");
        assertNotNull(record);
        assertEquals("Healthy", record.getDiagnosis());

        Vaccination vac = medicalService.recordVaccinationWithAutoDueDate(pet.getPetId(),
                "Rabies", LocalDate.now(), 12);
        assertNotNull(vac);
        assertEquals("Rabies", vac.getVaccineName());
        assertEquals(LocalDate.now().plusMonths(12), vac.getNextDueDate());

        Set<String> uniqueVaccines = medicalService.getUniqueVaccineTypes(pet.getPetId());
        assertTrue(uniqueVaccines.contains("Rabies"));
    }

    @Test
    @DisplayName("Throw BusinessRuleException when next vaccination due date is on or before vaccination date")
    void testVaccinationDateValidationThrowsBusinessRuleException() {
        LocalDate today = LocalDate.now();
        LocalDate invalidDueDate = today.minusDays(1);

        assertThrows(BusinessRuleException.class, () ->
                medicalService.recordVaccination(pet.getPetId(), "DHPP", today, invalidDueDate));
    }
}
