# Assessment 1 — Core Java Checkpoint: Pet Adoption & Veterinary Clinic System

A modular, in-memory console application implemented in **plain Core Java (Java 21 LTS)** built with **Apache Maven**, strictly adhering to the **Assessment 1 Core Java Checkpoint** rubric.

---

## 1. Project Overview & Architecture

The application is structured following clean Object-Oriented Programming (OOP) principles with decoupled architectural layers:

```text
Assess-1/
├── pom.xml                                      # Maven project configuration (Java 21, JUnit 5)
├── README.md                                    # Project documentation & execution guide
├── .gitignore                                   # Standard ignores for Java, Maven, and IDEs
├── run.bat                                      # One-click Windows build and launch script
├── docs/
│   └── ASSESSMENT-1-AUDIT.md                    # Rubric audit report
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
└── target/                                      # Maven compiled classes and JAR artifact (gitignored)
```

---

## 2. Core Java Checkpoint Rubric Alignment

### A. Object-Oriented Programming (OOP)
- **Encapsulation**: All entity attributes are private, exposed via getters/setters with strict defensive encapsulation (e.g. unmodifiable collections via `Collections.unmodifiableSet` and `Collections.unmodifiableList`).
- **Inheritance**: Abstract base class `User` encapsulates common fields (`id`, `name`, `email`, `phone`, `role`). Subclasses `Adopter`, `ShelterStaff`, and `Veterinarian` extend `User` with role-specific attributes and behaviors using `super(...)`.
- **Polymorphism**: `User` declares the abstract method `public abstract String getRoleDescription()`, overridden uniquely by each subclass.
- **Interfaces**: Domain operations are abstracted behind clean interfaces:
  - `Repository<T, ID>`
  - `PetService`
  - `AdoptionService`
  - `AppointmentService`
  - `MedicalService`

### B. Custom Exception Hierarchy
1. **Custom Checked Exception**:
   - `public class AdoptionException extends Exception`
   - Declared with `throws AdoptionException` in `AdoptionService` interface and implementation.
   - Thrown when attempting to adopt an unavailable/already adopted pet, submitting duplicate pending applications, or approving/rejecting invalid applications.
   - Caught explicitly and handled gracefully without swallowing in `ConsoleUI` and tested in JUnit.
2. **Custom Unchecked Exception**:
   - `public class PetNotFoundException extends EntityNotFoundException` (which extends `PetCareException extends RuntimeException`).
   - Thrown by `PetServiceImpl` whenever a lookup for a non-existent pet ID occurs.
   - Fully unchecked and compliant with `RuntimeException`.

### C. Deliberate Collection Usage (List, Set, Map, Queue)
The application deliberately and meaningfully uses all four required collections:
1. **`List`** (`ArrayList`):
   - Storing sequential domain records: `List<Pet>`, `List<AdoptionApplication>`, `List<Appointment>`, `List<MedicalRecord>`, `List<Vaccination>`.
2. **`Set`** (`LinkedHashSet` / `HashSet`):
   - Behavioral pet traits: `Pet.getTraits()` returns `Set<String>`.
   - Distinct species query: `PetService.getDistinctSpecies()` returns `Set<String>`.
   - Distinct breeds query: `PetService.getDistinctBreeds()` returns `Set<String>`.
   - Unique administered vaccines: `MedicalService.getUniqueVaccineTypes(petId)` returns `Set<String>`.
3. **`Map`** (`LinkedHashMap`):
   - O(1) primary key lookups: `Map<String, Pet>`, `Map<String, User>`, `Map<String, AdoptionApplication>`, `Map<String, Appointment>`.
4. **`Queue`** (`LinkedList`):
   - First-In, First-Out (FIFO) adoption application review queue: `AdoptionService.getApplicationQueue()` and `processNextApplicationInQueue(...)`.
   - Daily clinic patient check-in queue: `AppointmentService.getDailyQueue()`, `checkInAppointment(...)`, and `processNextAppointmentInQueue()`.

### D. Sorting (Comparable & Comparator)
- **`Comparable<Pet>`**: Implemented directly on `Pet` to define natural ordering alphabetically by pet name (with secondary tie-breaker by ID). Used via `PetService.getPetsSortedByName()` and `Collections.sort(pets)`.
- **`Comparator`**: Used for custom criteria sorting:
  - Sorting pets by age: `Comparator.comparingInt(Pet::getAge)`.
  - Sorting upcoming vaccinations by due date: `Comparator.comparing(Vaccination::getNextDueDate)`.

### E. Generics
- Generic repository interface `Repository<T, ID>` and in-memory implementation `InMemoryRepository<T, ID>`.
- Bounded wildcard implementation demonstrating PECS:
  ```java
  public void saveAll(List<? extends T> entities)
  ```
- Utilized in `PetServiceImpl` to manage entity storage without tightly coupling to raw map operations.

---

## 3. Two Core Workflows

### Flow 1: Pet Adoption Lifecycle
1. **Registration**: Shelter staff registers pets into the system with initial status `AVAILABLE` and personality traits (`Set<String>`).
2. **Catalog & Search**: Adopter browses available pets, filters by species/breed, and sorts by name or age.
3. **Application Submission**: Adopter submits an application (`AdoptionApplication`).
   - Validated: Pet must exist and must be `AVAILABLE` (throws `AdoptionException` if already adopted).
   - Validated: Adopter cannot have multiple active `PENDING` applications for the same pet.
   - Enqueued into the FIFO review queue (`Queue<AdoptionApplication>`).
4. **Application Review & Decision**: Shelter staff reviews pending applications by ID or sequentially through the FIFO queue.
   - **Approval**: Status changes to `APPROVED`, pet status changes to `ADOPTED`, pet owner is assigned to adopter, pet ID is added to adopter's owned list, and competing pending applications for the same pet are automatically marked `REJECTED`.
   - **Rejection**: Status changes to `REJECTED`, pet remains `AVAILABLE`.

### Flow 2: Veterinary Appointment & Vaccination Tracking
1. **Appointment Booking**: Adopter books an appointment for their owned pet with a licensed veterinarian (`Appointment`).
   - Validated: Pet must be adopted and owned by the requesting adopter (`BusinessRuleException`).
2. **Clinic Check-in**: Pet owner checks in for the day, entering the clinic's triage queue (`Queue<Appointment>`).
3. **Examination & Completion**: Veterinarian completes the appointment and creates an official `MedicalRecord` (visit date, diagnosis, treatment notes).
4. **Vaccination Administration**: Veterinarian administers a vaccine (`Vaccination`) with automatic or custom booster calculation.
   - Validated: Next due date must be strictly after the administration date (`BusinessRuleException`).
   - Queryable: Unique administered vaccines retrieved via `Set<String>`.

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

## 5. How to Build, Test, and Run

### Prerequisites
- **JDK 21** (or JDK 17+ compatible)
- **Apache Maven 3.8+**

### Build Commands

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
