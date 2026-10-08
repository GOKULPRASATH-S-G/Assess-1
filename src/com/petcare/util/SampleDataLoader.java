package com.petcare.util;

import com.petcare.model.Adopter;
import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;
import com.petcare.model.ShelterStaff;
import com.petcare.model.Veterinarian;
import com.petcare.service.PetService;
import com.petcare.service.UserService;

/**
 * Initializes the in-memory system with initial sample data matching specifications.
 */
public class SampleDataLoader {

    public static void loadSampleData(PetService petService, UserService userService) {
        // 1. Seed Sample Users
        Adopter john = new Adopter("A001", "John", "john@example.com", "555-0101");
        ShelterStaff sarah = new ShelterStaff("S001", "Sarah", "sarah@petcare.org", "555-0202", "Adoptions");
        Veterinarian drKumar = new Veterinarian("V001", "Dr. Kumar", "dr.kumar@vetcare.com", "555-0303",
                "Small Animal Internal Medicine", "VET-9824");

        userService.registerUser(john);
        userService.registerUser(sarah);
        userService.registerUser(drKumar);

        // Update ID generator counters for users
        IdGenerator.setCounterIfGreater("A", 1);
        IdGenerator.setCounterIfGreater("S", 1);
        IdGenerator.setCounterIfGreater("V", 1);

        // 2. Seed Sample Pets
        // P001 - Bruno - Dog - Labrador - 3 years - AVAILABLE
        // P002 - Luna  - Cat - Persian  - 2 years - AVAILABLE
        // P003 - Max   - Dog - Beagle   - 4 years - AVAILABLE
        Pet bruno = new Pet("P001", "Bruno", "Dog", "Labrador", 3, Gender.MALE, PetStatus.AVAILABLE, null);
        Pet luna = new Pet("P002", "Luna", "Cat", "Persian", 2, Gender.FEMALE, PetStatus.AVAILABLE, null);
        Pet max = new Pet("P003", "Max", "Dog", "Beagle", 4, Gender.MALE, PetStatus.AVAILABLE, null);

        petService.addPet(bruno);
        petService.addPet(luna);
        petService.addPet(max);

        // Update ID generator counter for pets
        IdGenerator.setCounterIfGreater("P", 3);
    }
}
