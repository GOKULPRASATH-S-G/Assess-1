# Assessment 1 — Core Java Checkpoint Checklist

This checklist maps each of the **9 assessment criteria (totaling 100 marks)** to the exact source files, classes, methods, and line evidence implemented in this repository.

---

## Rubric Breakdown & Evidence Directory

### 1. OOP Design (15 Marks)
- **Status:** **PASS**
- **Requirements:** Encapsulation, private fields, constructors, access modifiers, `this` keyword, class single responsibility, constants instead of magic numbers.
- **Evidence:**
  - `Pet.java`: Private fields (`petId`, `name`, `species`, `breed`, `age`, `gender`, `adoptionStatus`, `ownerId`, `traits`), parameterized constructors with `this(...)`, public getters, defensive unmodifiable set accessor `getTraits()`.
  - `AdoptionApplication.java`, `Appointment.java`, `MedicalRecord.java`, `Vaccination.java`: Complete encapsulation with private fields, immutability on primary IDs, controlled state transition methods.
  - Constants eliminating magic numbers:
    - `PetServiceImpl.java`: `MIN_PET_AGE = 0`, `PET_ID_PREFIX = "P"`.
    - `MedicalServiceImpl.java`: `DEFAULT_ANNUAL_BOOSTER_MONTHS = 12`, `VACCINATION_ID_PREFIX = "VAC"`, `MEDICAL_RECORD_ID_PREFIX = "MED"`.
    - `AdoptionServiceImpl.java`: `APPLICATION_ID_PREFIX = "APP"`, `DEFAULT_APPROVAL_NOTES`, `DEFAULT_REJECTION_NOTES`.
    - `AppointmentServiceImpl.java`: `APT_ID_PREFIX = "APT"`.

---

### 2. Inheritance & Polymorphism (15 Marks)
- **Status:** **PASS**
- **Requirements:** Meaningful inheritance hierarchy, method overriding, method overloading, interface-based programming, actual polymorphic resolution.
- **Evidence:**
  - **Inheritance Hierarchy:**
    - `com.petcare.model.User` (Abstract base class encapsulating shared identity: `id`, `name`, `email`, `phone`, `role`).
    - `com.petcare.model.Adopter` extends `User` (adds `adoptedPetIds` list, uses `super(...)`).
    - `com.petcare.model.ShelterStaff` extends `User` (adds `department`, uses `super(...)`).
    - `com.petcare.model.Veterinarian` extends `User` (adds `specialization`, `licenseNumber`, uses `super(...)`).
  - **Method Overriding & Dynamic Polymorphism:**
    - `public abstract String getRoleDescription()` in `User`, overridden with distinct role logic by `Adopter`, `ShelterStaff`, and `Veterinarian`.
    - Overridden `toString()`, `equals()`, and `hashCode()` across models.
  - **Method Overloading:**
    - `PetService`: `addPet(String, String, String, int, Gender)` vs `addPet(Pet)`; `getPetsSortedByAge()` vs `getPetsSortedByAge(boolean ascending)`.
    - `MedicalService`: `recordVaccination(petId, vaccine, date)` vs `recordVaccination(petId, vaccine, date, dueDate)` vs `recordVaccination(petId, vaccine, date, boosterMonths)`.
    - `AdoptionService`: `approveApplication(id)` vs `approveApplication(id, notes)`; `rejectApplication(id)` vs `rejectApplication(id, notes)`.
    - `AppointmentService`: `bookAppointment(...)` with/without custom reason.
  - **Interface-Based Programming:**
    - `Repository<T, ID>`, `PetService`, `AdoptionService`, `AppointmentService`, `MedicalService`.

---

### 3. Exception Handling (15 Marks)
- **Status:** **PASS**
- **Requirements:** 1 custom CHECKED exception, 1 custom UNCHECKED exception, proper `throw`/`throws`, specific non-swallowed catch blocks, clean error messages, try-with-resources.
- **Evidence:**
  - **Custom Checked Exception:**
    - `com.petcare.exception.AdoptionException extends Exception`.
    - Declared on method signatures: `AdoptionService#submitApplication(...) throws AdoptionException`, `approveApplication(...) throws AdoptionException`, `rejectApplication(...) throws AdoptionException`.
    - Thrown when attempting to adopt an unavailable pet or submitting duplicate applications.
    - Explicitly caught in `ConsoleUI.java` and `AdoptionFlowTest.java`.
  - **Custom Unchecked Exception:**
    - `com.petcare.exception.PetNotFoundException extends EntityNotFoundException` (which extends `PetCareException extends RuntimeException`).
    - Thrown in `PetServiceImpl#getPetById` when pet is missing.
  - **Exception Handling Strategy:**
    - Multi-catch in `ConsoleUI.java`: `catch (AdoptionException | EntityNotFoundException | ValidationException | BusinessRuleException e)`. No broad `catch (Exception e)`. Exceptions are never swallowed.
  - **Try-With-Resources:**
    - `ConsoleUI#start()` manages the interactive input scanner using `try (Scanner activeScanner = this.scanner) { ... }`.

---

### 4. Collections Framework (15 Marks)
- **Status:** **PASS**
- **Requirements:** Deliberate and meaningful use of ALL FOUR: `List`, `Set`, `Map`, `Queue`. Safe iteration and clear domain justification.
- **Evidence:**
  - **`List` (`ArrayList`)**:
    - Used in `PetService#getAllPets()`, `AdoptionService#getAllApplications()`, `AppointmentService#getAllAppointments()`, `MedicalService#getAllMedicalRecords()`.
    - *Why chosen:* Ordered sequential access for rendering tables and preserving collection order.
  - **`Set` (`LinkedHashSet` / `HashSet`)**:
    - `Pet#getTraits()` returns `Set<String>` of unique pet personality traits.
    - `PetService#getDistinctSpecies()` and `PetService#getDistinctBreeds()`.
    - `MedicalService#getUniqueVaccineTypes(petId)` returns `Set<String>`.
    - *Why chosen:* Eliminates duplicate values automatically and ensures uniqueness.
  - **`Map` (`LinkedHashMap`)**:
    - `InMemoryRepository` internal store `Map<ID, T> storage`.
    - `AdoptionServiceImpl.applications`, `AppointmentServiceImpl.appointments`, `MedicalServiceImpl.medicalRecords`.
    - *Why chosen:* Fast $O(1)$ key lookup by ID while preserving insertion order.
  - **`Queue` (`LinkedList`)**:
    - `AdoptionService#getApplicationQueue()` and `processNextApplicationInQueue(...)` for FIFO application triage.
    - `AppointmentService#getDailyQueue()`, `checkInAppointment(...)`, `processNextAppointmentInQueue()` for daily clinic patient waiting list.
    - *Why chosen:* Enforces First-In, First-Out (FIFO) processing to ensure fairness without applicant/patient starvation.

---

### 5. Generics (10 Marks)
- **Status:** **PASS**
- **Requirements:** Reusable generic class or interface, type parameters `<T, ID>`, bounded type or wildcards, no raw types.
- **Evidence:**
  - `com.petcare.repository.Repository<T, ID>`: Generic interface with type parameters.
  - `com.petcare.repository.InMemoryRepository<T, ID>`: In-memory generic implementation.
  - **Bounded Wildcard Method:**
    ```java
    void saveAll(List<? extends T> entities);
    ```
    Demonstrating PECS (Producer Extends) in `Repository.java` and `InMemoryRepository.java`.
  - Applied in `PetServiceImpl`: `private final Repository<Pet, String> petRepository`.

---

### 6. Language Fundamentals & Correctness (10 Marks)
- **Status:** **PASS**
- **Requirements:** Flow 1 (Pet Adoption) and Flow 2 (Veterinary Clinic) fully implemented and working in memory. Edge case validation (invalid IDs, boundary values, empty collections, state transitions).
- **Evidence:**
  - Flow 1 (Pet Adoption): Registration $\rightarrow$ Catalog browsing $\rightarrow$ Application submission $\rightarrow$ Approval/Rejection with auto-rejection of competing applications.
  - Flow 2 (Veterinary Clinic): Appointment booking for owned pets $\rightarrow$ Clinic check-in $\rightarrow$ Completion $\rightarrow$ Examination record $\rightarrow$ Vaccination administration with auto-due dates.
  - Edge cases handled: negative age, empty strings, adoption of unavailable pets, invalid status transitions, invalid next vaccination due dates (checked via `BusinessRuleException`).

---

### 7. Code Quality & Memory Awareness (10 Marks)
- **Status:** **PASS**
- **Requirements:** Meaningful names, small methods, proper packages, no compiler warnings, README explanation of Heap vs Stack and one memory pitfall.
- **Evidence:**
  - Zero compiler warnings (`mvn clean compile` passes with zero warnings).
  - Clear package decomposition: `model`, `service`, `repository`, `exception`, `ui`, `util`.
  - Documented in `README.md#6-memory-awareness`:
    - **Heap vs Stack:** Explanation of Stack frames for local primitives and references vs Heap memory allocation for objects.
    - **Memory Pitfall:** Memory leaks caused by retaining obsolete references in long-lived in-memory collections and static registries.

---

### 8. Git & Maven Hygiene (5 Marks)
- **Status:** **PASS**
- **Requirements:** Standard Maven project (`pom.xml`), Java 17 compatibility, proper `.gitignore` (ignoring `target/`, `*.class`, `.idea/`, `.vscode/`, `*.iml`), clean commit history.
- **Evidence:**
  - Standard Maven layout: `src/main/java`, `src/test/java`, `pom.xml`.
  - `pom.xml` configured for Java 17 release with JUnit 5 Jupiter and executable JAR packaging.
  - `.gitignore` ignores `target/`, `*.class`, `bin/`, `.idea/`, `.vscode/`, `*.iml`.
  - Git repository with meaningful commit messages tracking project evolution.

---

### 9. AI Review (5 Marks)
- **Status:** **PARTIAL** *(Code analysis, flaw identification, corrected implementation, and test verification complete; raw IDE Copilot capture to be attached if required by evaluator)*
- **Requirements:** Show one Copilot-generated snippet, explain flaw found and fixed, demonstrate human critical review.
- **Evidence:**
  - Fully documented in `docs/AI-REVIEW.md`.
  - **Prompt Documented:** `submitApplication` method with pet availability and duplicate check.
  - **Flaw Identified:** Using String reference equality (`==`) instead of `.equals()`, causing duplicate checking to fail on distinct runtime String instances; returning `null` silently; throwing raw `RuntimeException` instead of custom checked `AdoptionException`.
  - **Correction Implemented:** Using `.equalsIgnoreCase()`, input validation, FIFO `Queue` enqueueing, and `throws AdoptionException`.
  - **Automated Verification:** Verified by unit test `AdoptionFlowTest#testDuplicateApplicationWithDistinctStringObjectsThrowsAdoptionException`.

---

## Verification Commands
To verify the entire project locally:

```bash
# 1. Clean and run all 15 unit tests
mvn clean test

# 2. Package executable JAR
mvn package

# 3. Launch application
java -jar target/pet-adoption-veterinary-system-1.0.0.jar
```
