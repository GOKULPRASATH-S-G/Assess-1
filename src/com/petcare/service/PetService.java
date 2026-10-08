package com.petcare.service;

import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;

import java.util.List;

/**
 * Service interface specifying pet management operations.
 * Demonstrates Abstraction via Interface.
 */
public interface PetService {
    Pet addPet(String name, String species, String breed, int age, Gender gender);
    void addPet(Pet pet);
    Pet getPetById(String petId);
    List<Pet> getAllPets();
    List<Pet> getAvailablePets();
    List<Pet> filterPets(String species, String breed);
    List<Pet> getPetsByOwner(String ownerId);
    void updatePetStatus(String petId, PetStatus newStatus, String ownerId);
}
