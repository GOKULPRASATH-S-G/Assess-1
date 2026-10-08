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
 * deliberate Set collections, Comparable, and Comparator sorting.
 */
public class PetServiceImpl implements PetService {
    private final Repository<Pet, String> petRepository;

    public PetServiceImpl() {
        this.petRepository = new InMemoryRepository<>(Pet::getPetId);
    }

    public PetServiceImpl(Repository<Pet, String> petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet addPet(String name, String species, String breed, int age, Gender gender) {
        validatePetInputs(name, species, breed, age);
        String petId = IdGenerator.nextId("P");
        Pet pet = new Pet(petId, name.trim(), species.trim(), breed.trim(), age, gender != null ? gender : Gender.MALE);
        petRepository.save(pet);
        return pet;
    }

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

    @Override
    public Set<String> getDistinctSpecies() {
        return petRepository.findAll().stream()
                .map(Pet::getSpecies)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<String> getDistinctBreeds() {
        return petRepository.findAll().stream()
                .map(Pet::getBreed)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public List<Pet> getPetsSortedByName() {
        List<Pet> list = new ArrayList<>(petRepository.findAll());
        Collections.sort(list); // Uses Comparable<Pet>
        return list;
    }

    @Override
    public List<Pet> getPetsSortedByAge() {
        return petRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Pet::getAge)) // Uses Comparator
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
        if (age < 0) {
            throw new ValidationException("Pet age cannot be negative. Provided: " + age);
        }
    }
}
