package com.petcare.service;

import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;

import java.util.List;
import java.util.Set;

/**
 * Service interface specifying pet management operations.
 * Demonstrates:
 * - Abstraction via Interface.
 * - Method Overloading on addPet and getPetsSortedByAge.
 * - Deliberate Set collection contracts for distinct species/breeds.
 * - Sorting via Comparable and Comparator.
 */
public interface PetService {
    // Overloaded addition methods
    Pet addPet(String name, String species, String breed, int age, Gender gender);
    void addPet(Pet pet);

    Pet getPetById(String petId);
    List<Pet> getAllPets();
    List<Pet> getAvailablePets();
    List<Pet> filterPets(String species, String breed);
    List<Pet> getPetsByOwner(String ownerId);
    void updatePetStatus(String petId, PetStatus newStatus, String ownerId);

    // Deliberate Set operations
    Set<String> getDistinctSpecies();
    Set<String> getDistinctBreeds();

    // Natural sorting via Comparable<Pet>
    List<Pet> getPetsSortedByName();

    // Overloaded sorting via Comparator
    List<Pet> getPetsSortedByAge();
    List<Pet> getPetsSortedByAge(boolean ascending);
}
