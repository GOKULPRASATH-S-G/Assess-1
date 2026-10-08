package com.petcare.model;

/**
 * Abstract base class representing a generic user in the system.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User {
    private final String id;
    private String name;
    private String email;
    private String phone;
    private final UserRole role;

    public User(String id, String name, String email, String phone, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    /**
     * Abstract method to be implemented by specific user subclasses.
     * Demonstrates Polymorphism and Method Overriding.
     */
    public abstract String getRoleDescription();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", id, name, role.getDisplayName(), email);
    }
}
