# Assessment 1 — Core Java Checkpoint Rubric Audit

**Project:** Pet Adoption & Veterinary Clinic Management System  
**Version:** 1.0.0  
**JDK Version:** Java 21 LTS  
**Build Tool:** Apache Maven 3.9+  
**Execution Environment:** Pure Core Java Console Application (In-Memory)  

---

## Rubric Compliance Summary Table

| Rubric | Status | Evidence |
|---|---|---|
| **OOP Design** | **PASS** | Strict encapsulation with private fields, public getters/defensive accessors across all entities (`Pet`, `User`, `AdoptionApplication`, `Appointment`, `MedicalRecord`, `Vaccination`). Explicit `this` and `super` constructors. Cohesive package organization (`model`, `service`, `repository`, `exception`, `ui`, `util`). |
| **Inheritance & Polymorphism** | **PASS** | Abstract base class `User` extended by `Adopter`, `ShelterStaff`, and `Veterinarian`. Polymorphic method overriding on `getRoleDescription()`. Multiple domain interfaces (`Repository<T, ID>`, `PetService`, `AdoptionService`, `AppointmentService`, `MedicalService`) with clean caller decoupling. |
| **Exception Handling** | **PASS** | **Custom Checked Exception:** `AdoptionException extends Exception` declared with `throws` on business operations in `AdoptionService`, thrown on invalid adoption attempts, and caught explicitly in `ConsoleUI` and tests.<br>**Custom Unchecked Exception:** `PetNotFoundException extends EntityNotFoundException` (`RuntimeException`). Specific multi-catch blocks; no broad `catch (Exception e)` or swallowed exceptions. |
| **Collections** | **PASS** | Deliberate, domain-meaningful usage of all four required collections:<br>• **List (`ArrayList`)**: Sequential entity lists across all services.<br>• **Set (`LinkedHashSet`/`HashSet`)**: `Pet.getTraits()`, `PetService.getDistinctSpecies()`, `PetService.getDistinctBreeds()`, `MedicalService.getUniqueVaccineTypes()`.<br>• **Map (`LinkedHashMap`)**: O(1) primary key lookups for entities across repositories and services.<br>• **Queue (`LinkedList`)**: FIFO adoption application triage queue (`AdoptionService.getApplicationQueue()`) and clinic patient check-in queue (`AppointmentService.getDailyQueue()`). |
| **Generics** | **PASS** | Generic repository interface `Repository<T, ID>` and in-memory implementation `InMemoryRepository<T, ID>`. Demonstrates type parameters (`<T, ID>`) and bounded wildcards (`saveAll(List<? extends T> entities)`). Integrated into `PetServiceImpl`. |
| **Sorting** | **PASS** | `Pet implements Comparable<Pet>` for natural alphabetical sorting. `Comparator.comparingInt(Pet::getAge)` for age sorting. `Comparator.comparing(Vaccination::getNextDueDate)` for upcoming vaccination scheduling. |
| **Language Fundamentals** | **PASS** | Flow 1 (Pet Adoption) and Flow 2 (Veterinary Appointment & Vaccination) fully implemented in-memory without external frameworks (Spring, DB, JPA, Hibernate). Pure Core Java 21 console application. |
| **Code Quality** | **PASS** | Clean standard Maven layout (`src/main/java`, `src/test/java`). Enums for domain states (`PetStatus`, `ApplicationStatus`, `AppointmentStatus`, `UserRole`, `Gender`). Defensive copying (`Collections.unmodifiableSet/List`). |
| **Git & Maven** | **PASS** | Clean Maven `pom.xml` configured for Java 21 compiler release, JUnit 5 testing, and executable JAR generation (`com.petcare.Main`). Validated commands: `mvn clean compile`, `mvn test` (13 tests pass, 0 failures), `mvn package` (generates executable JAR in `target/`). Git repository initialized with comprehensive `.gitignore`. |
| **AI Review** | **PARTIAL** | Documented in `docs/AI-REVIEW.md`: Copilot prompt, generated snippet, technical flaws identified (String `==` reference equality bug, silent `null` return, and missing custom checked exception), corrected implementation, and automated JUnit 5 verification. Note: actual raw Copilot output/screenshot must be captured and attached before final submission if requested by evaluator. |

---

## Detailed Evidence by Criteria

### 1. Object-Oriented Programming (OOP)
- **`com.petcare.model.User`**: `abstract` class encapsulating common identity properties (`id`, `name`, `email`, `phone`, `role`).
- **`com.petcare.model.Adopter`**: Extends `User` using `super(id, name, email, phone, UserRole.ADOPTER)` and encapsulates owned pet IDs (`List<String> adoptedPetIds`).
- **`com.petcare.model.ShelterStaff`**: Extends `User` using `super(...)` and encapsulates `department`.
- **`com.petcare.model.Veterinarian`**: Extends `User` using `super(...)` and encapsulates `specialization` and `licenseNumber`.
- **Polymorphism**: Abstract method `public abstract String getRoleDescription()` implemented by `Adopter`, `ShelterStaff`, and `Veterinarian`.

### 2. Exceptions
- **Checked Exception:**
  ```java
  package com.petcare.exception;
  public class AdoptionException extends Exception { ... }
  ```
  - Used in: `AdoptionService.submitApplication(...) throws AdoptionException`
  - Used in: `AdoptionService.approveApplication(...) throws AdoptionException`
  - Used in: `AdoptionService.rejectApplication(...) throws AdoptionException`
  - Used in: `AdoptionService.processNextApplicationInQueue(...) throws AdoptionException`
- **Unchecked Exception:**
  ```java
  package com.petcare.exception;
  public class PetNotFoundException extends EntityNotFoundException { ... }
  ```
  - Used in: `PetServiceImpl.getPetById(String petId)`
- **Exception Handling in UI & Tests:**
  - Handled via specific multi-catch: `catch (AdoptionException | EntityNotFoundException | ValidationException | BusinessRuleException e)`

### 3. Collections (List, Set, Map, Queue)
- **`List`**: `List<Pet>`, `List<AdoptionApplication>`, `List<Appointment>`, `List<MedicalRecord>`, `List<Vaccination>` using `java.util.ArrayList`.
- **`Set`**:
  - `Pet.getTraits()` returns `Set<String>` (e.g., "Friendly", "Trained", "Active").
  - `PetService.getDistinctSpecies()` returns `Set<String>`.
  - `PetService.getDistinctBreeds()` returns `Set<String>`.
  - `MedicalService.getUniqueVaccineTypes(String petId)` returns `Set<String>`.
- **`Map`**:
  - `InMemoryRepository` backs storage with `Map<ID, T> storage = new LinkedHashMap<>()`.
  - Service registries use `Map<String, User>`, `Map<String, AdoptionApplication>`, `Map<String, Appointment>`, `Map<String, MedicalRecord>`, `Map<String, Vaccination>`.
- **`Queue`**:
  - `AdoptionService`: `Queue<AdoptionApplication>` using `java.util.LinkedList` for FIFO review triage (`getApplicationQueue()`, `processNextApplicationInQueue(...)`).
  - `AppointmentService`: `Queue<Appointment>` using `java.util.LinkedList` for daily patient check-in triage (`getDailyQueue()`, `checkInAppointment(...)`, `processNextAppointmentInQueue()`).

### 4. Generics
- Interface:
  ```java
  public interface Repository<T, ID> {
      T save(T entity);
      Optional<T> findById(ID id);
      List<T> findAll();
      boolean existsById(ID id);
      boolean deleteById(ID id);
      int count();
      void saveAll(List<? extends T> entities); // Bounded wildcard
  }
  ```
- Implementation: `com.petcare.repository.InMemoryRepository<T, ID>` utilized by `PetServiceImpl`.

### 5. Sorting
- **`Comparable<Pet>`**: Implemented on `Pet` with `compareTo(Pet other)` sorting alphabetically by name.
- **`Comparator`**:
  - `PetService.getPetsSortedByAge()` uses `Comparator.comparingInt(Pet::getAge)`.
  - `MedicalService.getUpcomingVaccinations()` uses `Comparator.comparing(Vaccination::getNextDueDate)`.

### 6. Build and Automated Tests
- `mvn clean compile`: SUCCESS (35 source files compiled under Java 21).
- `mvn test`: SUCCESS (14 tests run, 0 failures, 0 errors, 0 skipped).
- `mvn package`: SUCCESS (Generates `target/pet-adoption-veterinary-system-1.0.0.jar` with manifest Main-Class `com.petcare.Main`).

### 7. AI Review Evidence (Rubric 5 Marks)
- Full documentation available in `docs/AI-REVIEW.md`.
- **Target Area**: `com.petcare.service.AdoptionServiceImpl#submitApplication`.
- **Prompt Documented**: Method for submitting an adoption application with duplicate prevention and pet availability check.
- **Flaws Identified & Explained**:
  1. `==` reference comparison on strings causing duplicate check to fail on runtime String instances.
  2. Silent failure by returning `null` leading to `NullPointerException`.
  3. Failure to use custom checked exception `AdoptionException`.
  4. Missing parameter validation and lack of FIFO review `Queue` integration.
- **Corrected Code**: Uses `.equalsIgnoreCase()`, explicit null/blank checks, thread-safe atomic ID generation, enqueues to `Queue<AdoptionApplication>`, and declares `throws AdoptionException`.
- **Verified by Test**: `AdoptionFlowTest#testDuplicateApplicationWithDistinctStringObjectsThrowsAdoptionException`.
- **Status**: **PARTIAL** — All code, flaw analysis, fix, and unit test verification are complete in repo. Actual raw IDE Copilot output/screenshot should be captured and attached if required by evaluator.
