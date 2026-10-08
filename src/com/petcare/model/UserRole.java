package com.petcare.model;

/**
 * Enumeration representing user roles in the system.
 */
public enum UserRole {
    SHELTER_STAFF("Shelter Staff"),
    ADOPTER("Adopter / Pet Owner"),
    VETERINARIAN("Veterinarian");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
