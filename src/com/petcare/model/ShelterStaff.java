package com.petcare.model;

/**
 * Represents shelter staff authorized to add pets and manage adoption applications.
 * Inherits from User.
 */
public class ShelterStaff extends User {
    private String department;

    public ShelterStaff(String id, String name, String email, String phone, String department) {
        super(id, name, email, phone, UserRole.SHELTER_STAFF);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRoleDescription() {
        return "Shelter Staff in department: " + department;
    }
}
