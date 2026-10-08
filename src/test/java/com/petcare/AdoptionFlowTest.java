package com.petcare;

import com.petcare.exception.AdoptionException;
import com.petcare.model.Adopter;
import com.petcare.model.AdoptionApplication;
import com.petcare.model.ApplicationStatus;
import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;
import com.petcare.service.AdoptionService;
import com.petcare.service.AdoptionServiceImpl;
import com.petcare.service.PetService;
import com.petcare.service.PetServiceImpl;
import com.petcare.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flow 1: Pet Adoption Lifecycle & Validation Tests")
public class AdoptionFlowTest {

    private PetService petService;
    private UserService userService;
    private AdoptionService adoptionService;
    private Adopter adopter1;
    private Adopter adopter2;
    private Pet pet;

    @BeforeEach
    void setUp() {
        petService = new PetServiceImpl();
        userService = new UserService();
        adoptionService = new AdoptionServiceImpl(petService, userService);

        adopter1 = new Adopter("A101", "Alice Smith", "alice@example.com", "555-1111");
        adopter2 = new Adopter("A102", "Bob Jones", "bob@example.com", "555-2222");
        userService.registerUser(adopter1);
        userService.registerUser(adopter2);

        pet = petService.addPet("Bella", "Dog", "Golden Retriever", 2, Gender.FEMALE);
    }

    @Test
    @DisplayName("Successfully submit adoption application and verify status is PENDING")
    void testSubmitAdoptionApplicationSuccess() throws AdoptionException {
        AdoptionApplication app = adoptionService.submitApplication(pet.getPetId(), adopter1.getId(), "Spacious backyard");
        assertNotNull(app);
        assertEquals(ApplicationStatus.PENDING, app.getStatus());
        assertEquals(pet.getPetId(), app.getPetId());
        assertEquals(adopter1.getId(), app.getAdopterId());
    }

    @Test
    @DisplayName("Approve adoption application updates pet status and auto-rejects competing applications")
    void testApproveAdoptionApplicationSuccess() throws AdoptionException {
        AdoptionApplication app1 = adoptionService.submitApplication(pet.getPetId(), adopter1.getId(), "Loving home");
        AdoptionApplication app2 = adoptionService.submitApplication(pet.getPetId(), adopter2.getId(), "Experienced dog owner");

        AdoptionApplication approved = adoptionService.approveApplication(app1.getApplicationId(), "Home check passed");
        assertEquals(ApplicationStatus.APPROVED, approved.getStatus());

        Pet updatedPet = petService.getPetById(pet.getPetId());
        assertEquals(PetStatus.ADOPTED, updatedPet.getAdoptionStatus());
        assertEquals(adopter1.getId(), updatedPet.getOwnerId());
        assertTrue(adopter1.ownsPet(pet.getPetId()));

        AdoptionApplication rejectedComp = adoptionService.getApplicationById(app2.getApplicationId());
        assertEquals(ApplicationStatus.REJECTED, rejectedComp.getStatus());
    }

    @Test
    @DisplayName("Throw AdoptionException when attempting to adopt an already adopted pet")
    void testSubmitApplicationForUnavailablePetThrowsAdoptionException() throws AdoptionException {
        AdoptionApplication app = adoptionService.submitApplication(pet.getPetId(), adopter1.getId(), "Home A");
        adoptionService.approveApplication(app.getApplicationId(), "Approved");

        assertThrows(AdoptionException.class, () ->
                adoptionService.submitApplication(pet.getPetId(), adopter2.getId(), "Home B"));
    }

    @Test
    @DisplayName("Verify deliberate Queue usage for FIFO application review")
    void testAdoptionApplicationQueueFifoProcessing() throws AdoptionException {
        AdoptionApplication app1 = adoptionService.submitApplication(pet.getPetId(), adopter1.getId(), "Applicant 1");
        AdoptionApplication app2 = adoptionService.submitApplication(pet.getPetId(), adopter2.getId(), "Applicant 2");

        Queue<AdoptionApplication> queue = adoptionService.getApplicationQueue();
        assertFalse(queue.isEmpty());
        assertEquals(app1.getApplicationId(), queue.peek().getApplicationId());

        AdoptionApplication processed = adoptionService.processNextApplicationInQueue(true, "First in queue approved");
        assertEquals(app1.getApplicationId(), processed.getApplicationId());
        assertEquals(ApplicationStatus.APPROVED, processed.getStatus());
    }

    @Test
    @DisplayName("Verify duplicate application prevention with distinct String objects throws AdoptionException (AI Review Fix)")
    void testDuplicateApplicationWithDistinctStringObjectsThrowsAdoptionException() throws AdoptionException {
        // Initial application
        adoptionService.submitApplication(pet.getPetId(), adopter1.getId(), "First application");

        // Submit second application using newly allocated String objects (different reference address)
        String samePetIdNewInstance = new String(pet.getPetId());
        String sameAdopterIdNewInstance = new String(adopter1.getId());

        // Verifying reference inequality to prove test condition
        assertNotSame(pet.getPetId(), samePetIdNewInstance, "String objects must have different references");

        // Correct implementation uses .equalsIgnoreCase(), so duplicate is caught and AdoptionException is thrown
        AdoptionException ex = assertThrows(AdoptionException.class, () ->
                adoptionService.submitApplication(samePetIdNewInstance, sameAdopterIdNewInstance, "Second application"));

        assertTrue(ex.getMessage().contains("already has an active pending application"));
    }
}
