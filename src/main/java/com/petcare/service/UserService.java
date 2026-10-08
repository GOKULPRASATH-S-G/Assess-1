package com.petcare.service;

import com.petcare.exception.EntityNotFoundException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Adopter;
import com.petcare.model.ShelterStaff;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.model.Veterinarian;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service managing user identities (Adopters, Shelter Staff, Veterinarians).
 */
public class UserService {
    private final Map<String, User> users = new LinkedHashMap<>();

    public void registerUser(User user) {
        if (user == null) {
            throw new ValidationException("User cannot be null.");
        }
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            throw new ValidationException("User ID cannot be empty.");
        }
        users.put(user.getId(), user);
    }

    public User getUserById(String id) {
        if (id == null || !users.containsKey(id)) {
            throw new EntityNotFoundException("User with ID '" + id + "' not found.");
        }
        return users.get(id);
    }

    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Adopter getAdopterById(String id) {
        User user = getUserById(id);
        if (!(user instanceof Adopter)) {
            throw new ValidationException("User '" + id + "' is not an Adopter.");
        }
        return (Adopter) user;
    }

    public ShelterStaff getShelterStaffById(String id) {
        User user = getUserById(id);
        if (!(user instanceof ShelterStaff)) {
            throw new ValidationException("User '" + id + "' is not a Shelter Staff member.");
        }
        return (ShelterStaff) user;
    }

    public Veterinarian getVeterinarianById(String id) {
        User user = getUserById(id);
        if (!(user instanceof Veterinarian)) {
            throw new ValidationException("User '" + id + "' is not a Veterinarian.");
        }
        return (Veterinarian) user;
    }

    public List<Adopter> getAllAdopters() {
        List<Adopter> adopters = new ArrayList<>();
        for (User u : users.values()) {
            if (u instanceof Adopter) {
                adopters.add((Adopter) u);
            }
        }
        return adopters;
    }

    public List<ShelterStaff> getAllShelterStaff() {
        List<ShelterStaff> staff = new ArrayList<>();
        for (User u : users.values()) {
            if (u instanceof ShelterStaff) {
                staff.add((ShelterStaff) u);
            }
        }
        return staff;
    }

    public List<Veterinarian> getAllVeterinarians() {
        List<Veterinarian> vets = new ArrayList<>();
        for (User u : users.values()) {
            if (u instanceof Veterinarian) {
                vets.add((Veterinarian) u);
            }
        }
        return vets;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
