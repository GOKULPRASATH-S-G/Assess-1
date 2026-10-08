# Assessment 1 — Core Java Checkpoint: Pet Adoption & Veterinary Clinic Portal

A modular, in-memory console application implemented in **pure Core Java (Java 17 compatible)** built with **Apache Maven**, adhering strictly to the **Assessment 1 Core Java Checkpoint** rubric.

---

## 1. Project Overview & Architecture

The application is structured into cohesive packages with decoupled architectural layers:

```text
Assess-1/
├── pom.xml                                      # Maven configuration (Java 17, JUnit 5 Jupiter)
├── README.md                                    # Project documentation & execution guide
├── .gitignore                                   # Standard ignores for target/, *.class, and IDEs
├── run.bat                                      # One-click Windows build and launch script
├── docs/
│   ├── ASSESSMENT-1-CHECKLIST.md                # Complete 9-criteria assessment evidence checklist
│   ├── ASSESSMENT-1-AUDIT.md                    # Rubric audit report
│   └── AI-REVIEW.md                             # Copilot snippet review & flaw analysis
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── petcare/
│   │               ├── Main.java                # Application bootstrap & entry point
│   │               ├── model/                   # Domain entities and enums
│   │               │   ├── User.java            # Abstract base class
│   │               │   ├── UserRole.java        # Role enum (SHELTER_STAFF, ADOPTER, VETERINARIAN)
│   │               │   ├── Adopter.java         # Subclass of User
│   │               │   ├── ShelterStaff.java    # Subclass of User
│   │               │   ├── Veterinarian.java    # Subclass of User
│   │               │   ├── Pet.java             # Entity with Comparable & Set<String> traits
│   │               │   ├── PetStatus.java       # Enum (AVAILABLE, ADOPTED)
│   │               │   ├── Gender.java          # Enum (MALE, FEMALE)
│   │               │   ├── AdoptionApplication.java # Application entity
│   │               │   ├── ApplicationStatus.java   # Enum (PENDING, APPROVED, REJECTED)
│   │               │   ├── Appointment.java         # Veterinary appointment entity
│   │               │   ├── AppointmentStatus.java   # Enum (BOOKED, COMPLETED, CANCELLED)
│   │               │   ├── MedicalRecord.java       # Examination visit record entity
│   │               │   └── Vaccination.java         # Vaccination & booster record entity
│   │               ├── exception/               # Custom checked & unchecked exceptions
│   │               │   ├── AdoptionException.java        # Custom CHECKED exception (extends Exception)
│   │               │   ├── PetCareException.java         # Base unchecked exception
│   │               │   ├── PetNotFoundException.java     # Custom UNCHECKED exception
│   │               │   ├── EntityNotFoundException.java  # Entity lookup failure
│   │               │   ├── BusinessRuleException.java    # Business invariant failure
│   │               │   └── ValidationException.java      # Input validation failure
│   │               ├── repository/              # Generic In-Memory Repository
│   │               │   ├── Repository.java          # Generic interface <T, ID>
│   │               │   └── InMemoryRepository.java  # Generic implementation with bounded wildcards
│   │               ├── service/                 # Business logic interfaces & implementations
│   │               │   ├── UserService.java             # User identity service
│   │               │   ├── PetService.java              # Pet service interface
│   │               │   ├── PetServiceImpl.java          # In-memory implementation with Repository
│   │               │   ├── AdoptionService.java         # Adoption service interface with Queue
│   │               │   ├── AdoptionServiceImpl.java     # In-memory implementation
│   │               │   ├── AppointmentService.java      # Appointment service interface with Queue
│   │               │   ├── AppointmentServiceImpl.java  # In-memory implementation
│   │               │   ├── MedicalService.java          # Medical service interface with Set queries
│   │               │   └── MedicalServiceImpl.java      # In-memory implementation
│   │               ├── util/                    # Utilities & seeders
│   │               │   ├── IdGenerator.java             # Atomic ID generation (P001, APP001, etc.)
│   │               │   └── SampleDataLoader.java        # Initial sample record loader
│   │               └── ui/                      # Presentation layer
│   │                   └── ConsoleUI.java               # Interactive menu-driven CLI
│   └── test/
│       └── java/
│           └── com/
│               └── petcare/
│                   ├── AdoptionFlowTest.java        # Flow 1 JUnit 5 lifecycle tests
│                   ├── VeterinaryFlowTest.java      # Flow 2 JUnit 5 lifecycle tests
│                   └── RubricComplianceTest.java    # Rubric criteria verification tests
└── target/                                      # Maven build artifacts (gitignored)
```

---

## 2. Core Java Checkpoint Rubric Alignment

### A. Object-Oriented Programming (OOP)
- **Encapsulation**: All fields in models (`Pet`, `User`, `AdoptionApplication`, `Appointment`, `MedicalRecord`, `Vaccination`) are private. Access is mediated via getters, and mutations are controlled. Collections returned defensively via `Collections.unmodifiableSet` and `Collections.unmodifiableList`.
- **Constructors**: Proper parameterized constructors with explicit `this(...)` constructor chaining.
- **Access Modifiers**: Meaningful use of `public`, `protected`, and `private` across classes and packages.
- **Single Responsibility**: Clean division between Model (data), Service (business rules), Repository (storage), Exception (error contracts), and UI (console presentation).
- **Constants**: Fixed business constants eliminate magic numbers (e.g. `MIN_PET_AGE = 0`, `DEFAULT_ANNUAL_BOOSTER_MONTHS = 12`, `APPLICATION_ID_PREFIX = "APP"`).

### B. Inheritance & Polymorphism
- **Inheritance Hierarchy**:
  - `User` (`abstract` base class) encapsulates shared user identity (`id`, `name`, `email`, `phone`, `role`).
  - `Adopter` extends `User` (tracks owned pet IDs, calls `super(...)`).
  - `ShelterStaff` extends `User` (adds department, calls `super(...)`).
  - `Veterinarian` extends `User` (adds specialization and license number, calls `super(...)`).
- **Method Overriding (Dynamic Polymorphism)**:
  - `public abstract String getRoleDescription()` in `User` is overridden uniquely by each subclass.
  - Overridden `toString()`, `equals()`, and `hashCode()`.
- **Method Overloading (Static Polymorphism)**:
  - `PetService`: `addPet(String, String, String, int, Gender)` vs `addPet(Pet)`; `getPetsSortedByAge()` vs `getPetsSortedByAge(boolean ascending)`.
  - `MedicalService`: `recordVaccination(petId, vaccine, date)` vs `recordVaccination(petId, vaccine, date, dueDate)` vs `recordVaccination(petId, vaccine, date, boosterMonths)`.
  - `AdoptionService`: `approveApplication(id)` vs `approveApplication(id, notes)`; `rejectApplication(id)` vs `rejectApplication(id, notes)`.
  - `AppointmentService`: `bookAppointment(...)` with/without custom reason.
- **Interface-Based Programming**:
  - High-level callers interact through interfaces: `PetService`, `AdoptionService`, `AppointmentService`, `MedicalService`, and `Repository<T, ID>`.

### C. Custom Exception Handling
1. **Custom Checked Exception**:
   - `public class AdoptionException extends Exception`
   - Declared with `throws AdoptionException` in `AdoptionService` interface and implementation.
   - Thrown when attempting to adopt an already adopted pet or submitting duplicate pending applications.
   - Explicitly caught in `ConsoleUI` and tested via JUnit 5.
2. **Custom Unchecked Exception**:
   - `public class PetNotFoundException extends EntityNotFoundException` (which extends `PetCareException extends RuntimeException`).
   - Thrown by `PetServiceImpl` when a pet lookup fails.
3. **Exception Strategy**:
   - Specific multi-catch in `ConsoleUI`: `catch (AdoptionException | EntityNotFoundException | ValidationException | BusinessRuleException e)`.
   - No broad `catch (Exception e)`. Exceptions are never swallowed.
4. **Try-With-Resources**:
   - `ConsoleUI#start()` manages standard input using `try (Scanner activeScanner = this.scanner) { ... }`.

### D. Collections Framework (List, Set, Map, Queue)

| Collection | Implementation | Domain Purpose | Technical Reason for Selection |
|---|---|---|---|
| **`List`** | `java.util.ArrayList` | `List<Pet>`, `List<AdoptionApplication>`, `List<Appointment>`, `List<MedicalRecord>`, `List<Vaccination>` | Maintains ordered sequential access, preserves insertion sequence, and provides fast index-based traversal for console tables. |
| **`Set`** | `java.util.LinkedHashSet` | Pet traits (`Pet.getTraits()`), `getDistinctSpecies()`, `getDistinctBreeds()`, `getUniqueVaccineTypes()` | Guarantees uniqueness automatically, eliminating redundant entries without manual de-duplication loops. |
| **`Map`** | `java.util.LinkedHashMap` | `storage` in `InMemoryRepository`, `applications`, `appointments`, `medicalRecords` | Provides fast $O(1)$ constant-time lookup by unique entity identifier (`ID`) while preserving insertion order. |
| **`Queue`** | `java.util.LinkedList` | Adoption review queue (`applicationQueue`) and daily clinic triage queue (`checkInQueue`) | Implements First-In, First-Out (FIFO) processing to guarantee fair, sequential review of applications and patient arrivals without starvation. |

### E. Generics
- Generic repository interface `Repository<T, ID>` with type parameters for entity (`T`) and primary key (`ID`).
- In-memory generic implementation `InMemoryRepository<T, ID>` backed by a `LinkedHashMap`.
- Bounded wildcard method demonstrating PECS (Producer Extends):
  ```java
  public void saveAll(List<? extends T> entities)
  ```
- Used in `PetServiceImpl`: `Repository<Pet, String> petRepository`.

### F. Sorting (Comparable & Comparator)
- **Natural Ordering (`Comparable<Pet>`)**:
  - `Pet implements Comparable<Pet>` sorting alphabetically by pet name (with secondary tie-breaker by ID).
  - Used in `PetService#getPetsSortedByName()` via `Collections.sort(list)`.
- **Custom Ordering (`Comparator`)**:
  - Sorting pets by age: `Comparator.comparingInt(Pet::getAge)` in `PetService#getPetsSortedByAge()`.
  - Sorting upcoming vaccinations: `Comparator.comparing(Vaccination::getNextDueDate)` in `MedicalService#getUpcomingVaccinations()`.

---

## 3. Two Core Workflows

### Flow 1: Pet Adoption Lifecycle
1. **Pet Registration**: Shelter staff adds pets (`P001`, `P002`, `P003`) with status `AVAILABLE` and personality traits (`Set<String>`).
2. **Catalog Browsing**: Adopter searches available pets, filters by species/breed, or sorts by name or age.
3. **Application Submission**: Adopter submits an application (`AdoptionApplication`).
   - Validated: Pet must exist and must be `AVAILABLE` (throws `AdoptionException` if already adopted).
   - Validated: Adopter cannot have multiple active `PENDING` applications for the same pet.
   - Enqueued into the FIFO review queue (`Queue<AdoptionApplication>`).
4. **Application Review**: Shelter staff reviews pending applications by ID or sequentially through the FIFO queue.
   - **Approval**: Status changes to `APPROVED`, pet status changes to `ADOPTED`, pet owner is assigned, and competing pending applications for the same pet are automatically marked `REJECTED`.
   - **Rejection**: Status changes to `REJECTED`, pet remains `AVAILABLE`.

### Flow 2: Veterinary Appointment & Vaccination Tracking
1. **Appointment Booking**: Pet owner books an appointment for their adopted pet with a licensed veterinarian (`Appointment`).
   - Validated: Pet must be adopted and owned by the requesting adopter (`BusinessRuleException`).
2. **Clinic Check-in**: Owner checks in for the appointment, entering the clinic's triage queue (`Queue<Appointment>`).
3. **Examination & Completion**: Veterinarian completes the appointment and records an official `MedicalRecord` (visit date, diagnosis, treatment notes).
4. **Vaccination Administration**: Veterinarian administers a vaccine (`Vaccination`) with automatic (12-month) or custom booster interval.
   - Validated: Next due date must be strictly after administration date (`BusinessRuleException`).
   - Queryable: Distinct administered vaccines retrieved via `Set<String>`.

---

## 4. Preloaded Sample Data

On startup, the system seeds the following sample records:
- **Shelter Staff**: `S001` - Sarah (Adoptions Department)
- **Adopter**: `A001` - John (`john@example.com`)
- **Veterinarian**: `V001` - Dr. Kumar (`dr.kumar@vetcare.com`, Small Animal Internal Medicine, License: VET-9824)
- **Available Pets**:
  - `P001` - Bruno (Dog, Labrador, 3 yrs, Male, AVAILABLE, Traits: Friendly, Trained, Active)
  - `P002` - Luna (Cat, Persian, 2 yrs, Female, AVAILABLE, Traits: Gentle, Indoor, Calm)
  - `P003` - Max (Dog, Beagle, 4 yrs, Male, AVAILABLE, Traits: Loyal, Playful, Energetic)

---

## 5. Memory Awareness

### Heap vs. Stack Memory
- **Stack Memory**:
  - Used for thread execution and method call stack frames.
  - Stores local primitive variables (e.g. `int age`, `boolean approve`) and references to objects (e.g. `Pet pet`, `String petId`).
  - Stack allocation and de-allocation are automatic as methods enter and return (LIFO order).
- **Heap Memory**:
  - Used for dynamic object and array allocation (e.g. `new Pet(...)`, `new LinkedHashMap<>()`).
  - Objects created with `new` reside on the Heap, while their references are stored in Stack frames.
  - Managed by the Java Garbage Collector (GC), which frees objects that are no longer reachable from any GC root.

### Realistic Memory Pitfall: Unintentional Object Retention (Memory Leak)
- **Problem**: In-memory collections (such as `Map`, `List`, or `Queue`) that retain object references long after they are no longer needed prevent the Garbage Collector from reclaiming Heap memory.
- **Impact in Domain**: If applications, completed appointments, or canceled items are never removed from `Queue` or internal collections, the heap memory consumed grows indefinitely, eventually causing an `OutOfMemoryError`.
- **Mitigation Applied**: 
  - Periodic queue synchronization (`applicationQueue.removeIf(a -> !a.isPending())`).
  - Active queue polling (`queue.poll()`) when processing triage items.
  - Returning unmodifiable or shallow copy snapshots (`new ArrayList<>(storage.values())`) to prevent callers from corrupting internal collection references.

---

## 6. How to Build, Test, and Run

### Prerequisites
- **JDK 17 or higher** (JDK 17 LTS / JDK 21 LTS)
- **Apache Maven 3.8+**

### Maven Commands

1. **Compile all sources**:
   ```bash
   mvn clean compile
   ```

2. **Execute JUnit 5 automated test suite**:
   ```bash
   mvn test
   ```

3. **Package executable JAR**:
   ```bash
   mvn package
   ```
   The executable JAR is generated at:
   `target/pet-adoption-veterinary-system-1.0.0.jar`

### Running the Application

- **Via Executable JAR**:
  ```bash
  java -jar target/pet-adoption-veterinary-system-1.0.0.jar
  ```

- **Via Windows Batch Script**:
  ```cmd
  run.bat
  ```

---

## 7. Documentation Index

- [docs/ASSESSMENT-1-CHECKLIST.md](file:///c:/Users/gokul/Downloads/Assessment%201/docs/ASSESSMENT-1-CHECKLIST.md): Complete rubric checklist with file/line evidence for all 9 criteria (100 marks).
- [docs/ASSESSMENT-1-AUDIT.md](file:///c:/Users/gokul/Downloads/Assessment%201/docs/ASSESSMENT-1-AUDIT.md): Detailed rubric audit and assessment status.
- [docs/AI-REVIEW.md](file:///c:/Users/gokul/Downloads/Assessment%201/docs/AI-REVIEW.md): GitHub Copilot prompt, snippet review, flaw identification (`String ==` reference equality bug), and corrected implementation.
