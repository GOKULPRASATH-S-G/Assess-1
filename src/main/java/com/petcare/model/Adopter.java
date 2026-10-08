package com.petcare.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an adopter / pet owner in the system.
 * Inherits from User and tracks adopted pet IDs.
 */
public class Adopter extends User {
    private final List<String> adoptedPetIds;

    public Adopter(String id, String name, String email, String phone) {
        super(id, name, email, phone, UserRole.ADOPTER);
        this.adoptedPetIds = new ArrayList<>();
    }

    public List<String> getAdoptedPetIds() {
        return Collections.unmodifiableList(adoptedPetIds);
    }

    public void addAdoptedPet(String petId) {
        if (petId != null && !adoptedPetIds.contains(petId)) {
            adoptedPetIds.add(petId);
        }
    }

    public boolean ownsPet(String petId) {
        return adoptedPetIds.contains(petId);
    }

    @Override
    public String getRoleDescription() {
        return "Adopter/Pet Owner with " + adoptedPetIds.size() + " adopted pet(s).";
    }
}
