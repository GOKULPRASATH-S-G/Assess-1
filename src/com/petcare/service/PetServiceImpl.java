package com.petcare.service;

import com.petcare.exception.EntityNotFoundException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.PetStatus;
import com.petcare.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PetService using Map and List collections.
 */
public class PetServiceImpl implements PetService {
    private final Map<String, Pet> pets = new LinkedHashMap<>();

    @Override
    public Pet addPet(String name, String species, String breed, int age, Gender gender) {
        validatePetInputs(name, species, breed, age);
        String petId = IdGenerator.nextId("P");
        Pet pet = new Pet(petId, name.trim(), species.trim(), breed.trim(), age, gender != null ? gender : Gender.MALE);
        pets.put(pet.getPetId(), pet);
        return pet;
    }

    @Override
    public void addPet(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Pet cannot be null.");
        }
        validatePetInputs(pet.getName(), pet.getSpecies(), pet.getBreed(), pet.getAge());
        pets.put(pet.getPetId(), pet);
    }

    @Override
    public Pet getPetById(String petId) {
        if (petId == null || !pets.containsKey(petId)) {
            throw new EntityNotFoundException("Pet with ID '" + petId + "' not found.");
        }
        return pets.get(petId);
    }

    @Override
    public List<Pet> getAllPets() {
        return new ArrayList<>(pets.values());
    }

    @Override
    public List<Pet> getAvailablePets() {
        return pets.values().stream()
                .filter(Pet::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pet> filterPets(String species, String breed) {
        return pets.values().stream()
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
        return pets.values().stream()
                .filter(p -> ownerId.equals(p.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public void updatePetStatus(String petId, PetStatus newStatus, String ownerId) {
        Pet pet = getPetById(petId);
        pet.setAdoptionStatus(newStatus);
        pet.setOwnerId(ownerId);
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
