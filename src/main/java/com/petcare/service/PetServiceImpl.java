package com.petcare.service;

import com.petcare.exception.PetNotFoundException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;
import com.petcare.repository.InMemoryRepository;
import com.petcare.repository.Repository;
import com.petcare.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PetService utilizing a generic Repository,
 * deliberate Set collections, Comparable natural sorting, and Comparator custom sorting.
 * Demonstrates:
 * - Generic Repository<Pet, String> pattern decoupling storage.
 * - Map backing store inside repository for O(1) ID lookups.
 * - Set collection choice for distinct species and breeds.
 * - Method Overloading on addPet and getPetsSortedByAge.
 * - Constants for fixed business rules (MIN_PET_AGE).
 */
public class PetServiceImpl implements PetService {

    // Constants for fixed business rules and ID formatting
    public static final String PET_ID_PREFIX = "P";
    public static final int MIN_PET_AGE = 0;

    // Generic repository abstraction managing entities in memory
    private final Repository<Pet, String> petRepository;

    public PetServiceImpl() {
        this.petRepository = new InMemoryRepository<>(Pet::getPetId);
    }

    public PetServiceImpl(Repository<Pet, String> petRepository) {
        this.petRepository = petRepository;
    }

    // Overload 1: full field constructor with auto-generated ID
    @Override
    public Pet addPet(String name, String species, String breed, int age, Gender gender) {
        validatePetInputs(name, species, breed, age);
        String petId = IdGenerator.nextId(PET_ID_PREFIX);
        Pet pet = new Pet(petId, name.trim(), species.trim(), breed.trim(), age, gender != null ? gender : Gender.MALE);
        petRepository.save(pet);
        return pet;
    }

    // Overload 2: pre-instantiated entity
    @Override
    public void addPet(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Pet cannot be null.");
        }
        validatePetInputs(pet.getName(), pet.getSpecies(), pet.getBreed(), pet.getAge());
        petRepository.save(pet);
    }

    @Override
    public Pet getPetById(String petId) {
        if (petId == null) {
            throw new PetNotFoundException("Pet ID cannot be null.");
        }
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet with ID '" + petId + "' not found."));
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public List<Pet> getAvailablePets() {
        return petRepository.findAll().stream()
                .filter(Pet::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pet> filterPets(String species, String breed) {
        return petRepository.findAll().stream()
                .filter(Pet::isAvailable)
                .filter(p -> species == null || species.trim().isEmpty() ||
                        p.getSpecies().equalsIgnoreCase(species.trim()))
                .filter(p -> breed == null || breed.trim().isEmpty() ||
                        p.getBreed().equalsIgnoreCase(breed.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pet> getPetsByOwner(String ownerId) {
        if (ownerId == null) {
            return new ArrayList<>();
        }
        return petRepository.findAll().stream()
                .filter(p -> ownerId.equals(p.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public void updatePetStatus(String petId, PetStatus newStatus, String ownerId) {
        Pet pet = getPetById(petId);
        pet.setAdoptionStatus(newStatus);
        pet.setOwnerId(ownerId);
        petRepository.save(pet);
    }

    // Deliberate Set usage: guarantees uniqueness of species
    @Override
    public Set<String> getDistinctSpecies() {
        return petRepository.findAll().stream()
                .map(Pet::getSpecies)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // Deliberate Set usage: guarantees uniqueness of breeds
    @Override
    public Set<String> getDistinctBreeds() {
        return petRepository.findAll().stream()
                .map(Pet::getBreed)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // Natural sorting via Comparable<Pet> (by name)
    @Override
    public List<Pet> getPetsSortedByName() {
        List<Pet> list = new ArrayList<>(petRepository.findAll());
        Collections.sort(list); // Uses Comparable<Pet>
        return list;
    }

    // Overload 1: ascending age sorting using Comparator
    @Override
    public List<Pet> getPetsSortedByAge() {
        return getPetsSortedByAge(true);
    }

    // Overload 2: parameter-driven ascending/descending age sorting using Comparator
    @Override
    public List<Pet> getPetsSortedByAge(boolean ascending) {
        Comparator<Pet> comp = Comparator.comparingInt(Pet::getAge);
        if (!ascending) {
            comp = comp.reversed();
        }
        return petRepository.findAll().stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }

    private void validatePetInputs(String name, String species, String breed, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Pet name cannot be empty.");
        }
        if (species == null || species.trim().isEmpty()) {
            throw new ValidationException("Pet species cannot be empty.");
        }
        if (breed == null || breed.trim().isEmpty()) {
            throw new ValidationException("Pet breed cannot be empty.");
        }
        if (age < MIN_PET_AGE) {
            throw new ValidationException("Pet age cannot be negative. Provided: " + age);
        }
    }
}
