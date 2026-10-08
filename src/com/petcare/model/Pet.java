package com.petcare.model;

import java.util.Objects;

/**
 * Represents a pet registered in the shelter or veterinary clinic system.
 */
public class Pet {
    private final String petId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private Gender gender;
    private PetStatus adoptionStatus;
    private String ownerId; // null while in shelter; set to adopterId once adopted

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

    public boolean isAvailable() {
        return this.adoptionStatus == PetStatus.AVAILABLE;
    }

    public boolean isAdopted() {
        return this.adoptionStatus == PetStatus.ADOPTED;
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
        return String.format("%s - %s - %s - %s - %d year(s) - %s - %s%s",
                petId, name, species, breed, age, gender, adoptionStatus,
                (ownerId != null ? " (Owner: " + ownerId + ")" : ""));
    }
}
