package com.petcare.model;

/**
 * Represents a licensed veterinarian authorized to conduct medical visits and administer vaccinations.
 * Inherits from User.
 */
public class Veterinarian extends User {
    private String specialization;
    private String licenseNumber;

    public Veterinarian(String id, String name, String email, String phone, String specialization, String licenseNumber) {
        super(id, name, email, phone, UserRole.VETERINARIAN);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Override
    public String getRoleDescription() {
        return "Veterinarian (" + specialization + ", License: " + licenseNumber + ")";
    }
}
