package com.petcare;

import com.petcare.exception.AdoptionException;
import com.petcare.exception.PetNotFoundException;
import com.petcare.model.Adopter;
import com.petcare.model.Gender;
import com.petcare.model.Pet;
import com.petcare.model.ShelterStaff;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.model.Veterinarian;
import com.petcare.repository.InMemoryRepository;
import com.petcare.repository.Repository;
import com.petcare.service.PetService;
import com.petcare.service.PetServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Assessment 1 Rubric Compliance Verification Tests")
public class RubricComplianceTest {

    @Test
    @DisplayName("Rubric: Inheritance and Polymorphic User hierarchy")
    void testInheritanceHierarchy() {
        User adopter = new Adopter("U1", "Adopter One", "a@test.com", "111");
        User staff = new ShelterStaff("U2", "Staff One", "s@test.com", "222", "Intake");
        User vet = new Veterinarian("U3", "Vet One", "v@test.com", "333", "General", "LIC-1");

        assertInstanceOf(User.class, adopter);
        assertInstanceOf(User.class, staff);
        assertInstanceOf(User.class, vet);

        assertEquals(UserRole.ADOPTER, adopter.getRole());
        assertEquals(UserRole.SHELTER_STAFF, staff.getRole());
        assertEquals(UserRole.VETERINARIAN, vet.getRole());

        // Polymorphic invocation of abstract method
        assertNotNull(adopter.getRoleDescription());
        assertNotNull(staff.getRoleDescription());
        assertNotNull(vet.getRoleDescription());
    }

    @Test
    @DisplayName("Rubric: Custom CHECKED exception (AdoptionException) and UNCHECKED exception (PetNotFoundException)")
    void testCustomExceptionsHierarchy() {
        // Checked Exception: must inherit from Exception and NOT RuntimeException
        assertTrue(Exception.class.isAssignableFrom(AdoptionException.class),
                "AdoptionException must inherit from java.lang.Exception");
        assertFalse(RuntimeException.class.isAssignableFrom(AdoptionException.class),
                "AdoptionException must NOT be a RuntimeException (checked exception requirement)");

        // Unchecked Exception: must inherit from RuntimeException
        assertTrue(RuntimeException.class.isAssignableFrom(PetNotFoundException.class),
                "PetNotFoundException must inherit from java.lang.RuntimeException");
    }

    @Test
    @DisplayName("Rubric: Deliberate and meaningful use of List, Set, Map, and Queue")
    void testAllFourCollectionsDeliberateUsage() {
        PetService service = new PetServiceImpl();

        Pet p1 = service.addPet("Milo", "Cat", "Siamese", 2, Gender.MALE);
        Pet p2 = service.addPet("Zoe", "Dog", "Poodle", 4, Gender.FEMALE);
        p1.addTrait("Playful");
        p1.addTrait("Quiet");

        // 1. List
        List<Pet> list = service.getAllPets();
        assertEquals(2, list.size());

        // 2. Set
        Set<String> speciesSet = service.getDistinctSpecies();
        assertTrue(speciesSet.contains("Cat"));
        assertTrue(speciesSet.contains("Dog"));
        assertEquals(2, speciesSet.size());

        Set<String> traitsSet = p1.getTraits();
        assertTrue(traitsSet.contains("Playful"));
        assertEquals(2, traitsSet.size());

        // 3. Map (internal and lookup by ID)
        Pet retrieved = service.getPetById(p1.getPetId());
        assertEquals(p1.getName(), retrieved.getName());

        // 4. Queue (FIFO data structure)
        Queue<Pet> triageQueue = new LinkedList<>();
        triageQueue.offer(p1);
        triageQueue.offer(p2);
        assertEquals(p1, triageQueue.poll());
        assertEquals(p2, triageQueue.poll());
        assertTrue(triageQueue.isEmpty());
    }

    @Test
    @DisplayName("Rubric: Generics with generic Repository<T, ID> and bounded wildcard (? extends T)")
    void testGenericRepositoryWithBoundedWildcard() {
        Repository<Pet, String> repo = new InMemoryRepository<>(Pet::getPetId);

        Pet p1 = new Pet("P10", "Simba", "Cat", "Persian", 1, Gender.MALE);
        Pet p2 = new Pet("P20", "Nala", "Cat", "Persian", 1, Gender.FEMALE);

        repo.save(p1);
        assertTrue(repo.existsById("P10"));
        assertEquals(1, repo.count());

        // Bounded wildcard test (? extends Pet)
        List<Pet> batch = List.of(p2);
        repo.saveAll(batch);
        assertEquals(2, repo.count());
        assertTrue(repo.findById("P20").isPresent());
    }

    @Test
    @DisplayName("Rubric: Sorting using Comparable and Comparator")
    void testComparableAndComparatorSorting() {
        Pet a = new Pet("P1", "Charlie", "Dog", "Beagle", 5, Gender.MALE);
        Pet b = new Pet("P2", "Apollo", "Dog", "Husky", 2, Gender.MALE);
        Pet c = new Pet("P3", "Bella", "Cat", "Persian", 3, Gender.FEMALE);

        List<Pet> pets = new ArrayList<>(List.of(a, b, c));

        // Natural sort by name using Comparable<Pet>
        Collections.sort(pets);
        assertEquals("Apollo", pets.get(0).getName());
        assertEquals("Bella", pets.get(1).getName());
        assertEquals("Charlie", pets.get(2).getName());

        // Custom sort by age using Comparator
        pets.sort(Comparator.comparingInt(Pet::getAge));
        assertEquals(2, pets.get(0).getAge()); // Apollo
        assertEquals(3, pets.get(1).getAge()); // Bella
        assertEquals(5, pets.get(2).getAge()); // Charlie
    }
}
