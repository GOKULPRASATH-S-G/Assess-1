package com.petcare.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a pet registered in the shelter or veterinary clinic system.
 * Demonstrates Encapsulation, Comparable interface for natural sorting,
 * and deliberate Set usage for behavioral traits.
 */
public class Pet implements Comparable<Pet> {
    private final String petId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private Gender gender;
    private PetStatus adoptionStatus;
    private String ownerId; // null while in shelter; set to adopterId once adopted
    private final Set<String> traits = new LinkedHashSet<>();

    public Pet(String petId, String name, String species, String breed, int age, Gender gender) {
        this(petId, name, species, breed, age, gender, PetStatus.AVAILABLE, null);
    }

    public Pet(String petId, String name, String species, String breed, int age, Gender gender, PetStatus adoptionStatus, String ownerId) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.adoptionStatus = adoptionStatus;
        this.ownerId = ownerId;
    }

    public String getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public PetStatus getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(PetStatus adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Set<String> getTraits() {
        return Collections.unmodifiableSet(traits);
    }

    public void addTrait(String trait) {
        if (trait != null && !trait.trim().isEmpty()) {
            this.traits.add(trait.trim());
        }
    }

    public void addTraits(java.util.Collection<String> newTraits) {
        if (newTraits != null) {
            for (String t : newTraits) {
                addTrait(t);
            }
        }
    }

    public boolean isAvailable() {
        return this.adoptionStatus == PetStatus.AVAILABLE;
    }

    public boolean isAdopted() {
        return this.adoptionStatus == PetStatus.ADOPTED;
    }

    @Override
    public int compareTo(Pet other) {
        if (other == null) return 1;
        int cmp = this.name.compareToIgnoreCase(other.name);
        if (cmp != 0) return cmp;
        return this.petId.compareTo(other.petId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(petId, pet.petId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(petId);
    }

    @Override
    public String toString() {
        String traitStr = traits.isEmpty() ? "" : " | Traits: " + traits;
        return String.format("%s - %s - %s - %s - %d year(s) - %s - %s%s%s",
                petId, name, species, breed, age, gender, adoptionStatus,
                (ownerId != null ? " (Owner: " + ownerId + ")" : ""),
                traitStr);
    }
}
