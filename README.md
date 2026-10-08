# Pet Adoption & Veterinary Clinic Management System

A modular, console-based application implemented in **pure Java (Java 21/SE)** with **in-memory data structures**, following clean Object-Oriented Programming (OOP) principles.

---

## 1. Project Overview & Architecture

The application is structured into distinct architectural layers with high cohesion and loose coupling:

```text
src/
└── com/
    └── petcare/
        ├── Main.java                          # Bootstrap & entry point
        ├── model/                             # Domain Entities & Enums
        │   ├── User.java                      # Abstract base class
        │   ├── UserRole.java                  # Enum for roles
        │   ├── Adopter.java                   # Concrete User subclass
        │   ├── ShelterStaff.java              # Concrete User subclass
        │   ├── Veterinarian.java              # Concrete User subclass
        │   ├── Pet.java                       # Pet entity
        │   ├── PetStatus.java                 # Enum: AVAILABLE, ADOPTED
        │   ├── Gender.java                    # Enum: MALE, FEMALE
        │   ├── AdoptionApplication.java       # Application entity
        │   ├── ApplicationStatus.java         # Enum: PENDING, APPROVED, REJECTED
        │   ├── Appointment.java               # Appointment entity
        │   ├── AppointmentStatus.java         # Enum: BOOKED, COMPLETED, CANCELLED
        │   ├── MedicalRecord.java             # Medical examination visit entity
        │   └── Vaccination.java               # Vaccination & booster tracking entity
        ├── exception/                         # Domain-Specific Exception Hierarchy
        │   ├── PetCareException.java          # Base unchecked exception
        │   ├── EntityNotFoundException.java   # Entity lookup failures
        │   ├── ValidationException.java       # Field & format validation failures
        │   └── BusinessRuleException.java     # Business invariant violations
        ├── service/                           # Business Logic & Service Interfaces
        │   ├── UserService.java               # User directory & lookups
        │   ├── PetService.java                # Interface for pet operations
        │   ├── PetServiceImpl.java            # In-memory implementation
        │   ├── AdoptionService.java           # Interface for adoption rules
        │   ├── AdoptionServiceImpl.java       # In-memory implementation
        │   ├── AppointmentService.java        # Interface for vet appointments
        │   ├── AppointmentServiceImpl.java    # In-memory implementation
        │   ├── MedicalService.java            # Interface for clinic records
        │   └── MedicalServiceImpl.java        # In-memory implementation
        ├── util/                              # Utilities & Seeders
        │   ├── IdGenerator.java               # Auto-increment formatted IDs
        │   └── SampleDataLoader.java          # Sample data initializer
        └── ui/                                # Presentation Layer
            └── ConsoleUI.java                 # CLI menus & table formatting
```

---

## 2. Object-Oriented Programming (OOP) Principles

1. **Encapsulation**:
   - All domain entity attributes (`Pet`, `User`, `AdoptionApplication`, `Appointment`, `MedicalRecord`, `Vaccination`) are `private`.
   - Access and state mutations are controlled via public getter and setter methods with validation checks.
   - Immutable IDs and creation timestamps prevent accidental state corruption.

2. **Inheritance**:
   - `User` is an `abstract` base class encapsulating shared user data (`id`, `name`, `email`, `phone`, `role`).
   - `Adopter`, `ShelterStaff`, and `Veterinarian` extend `User`, adding role-specific attributes:
     - `Adopter` adds an owned pet tracking list (`List<String> adoptedPetIds`).
     - `ShelterStaff` adds `department`.
     - `Veterinarian` adds `specialization` and `licenseNumber`.
   - Custom exceptions form an inheritance hierarchy rooted at `PetCareException` extending `RuntimeException`.

3. **Polymorphism & Method Overriding**:
   - `User` defines the abstract method `public abstract String getRoleDescription()`, overridden uniquely by `Adopter`, `ShelterStaff`, and `Veterinarian`.
   - `toString()` is overridden across all models for clean representations.

4. **Abstraction & Interfaces**:
   - Business operations are decoupled behind clear Java interfaces: `PetService`, `AdoptionService`, `AppointmentService`, `MedicalService`.
   - Implementations (`*ServiceImpl`) hide collection-based storage and query details from the presentation layer (`ConsoleUI`).

5. **Type Safety with Enums**:
   - Enums (`PetStatus`, `ApplicationStatus`, `AppointmentStatus`, `UserRole`, `Gender`) strictly restrict domain states and prevent invalid status strings.

6. **Separation of Concerns**:
   - **Model Layer**: Holds data and entity state.
   - **Service Layer**: Enforces business rules and invariants.
   - **UI Layer**: Manages CLI menus, user prompts, and formatted tabular rendering.
   - **Exception Layer**: Decouples error signaling from error presentation.

---

## 3. Core Business Workflows

### Flow 1: Pet Adoption Workflow

```text
Shelter Staff
      ↓
Add Pet  → (Status: AVAILABLE)
      ↓
View Available Pets
      ↓
Adopter selects a pet (e.g., Bruno P001)
      ↓
Adopter submits adoption application (Status: PENDING, App ID: APP001)
      ↓
Shelter Staff reviews pending applications
      ↓
Shelter Staff Approves / Rejects application:
      ├── If Approved:
      │     • Application status → APPROVED
      │     • Pet status → ADOPTED
      │     • Pet ownerId set to Adopter ID
      │     • Pet added to Adopter's owned pets list
      │     • Other pending applications for same pet automatically rejected
      └── If Rejected:
            • Application status → REJECTED
            • Pet status remains AVAILABLE
```

**Enforced Business Rules & Validations:**
- Pet name, species, and breed cannot be empty; age cannot be negative.
- Cannot submit an application for an already adopted pet.
- Cannot submit multiple active (`PENDING`) applications for the same pet by the same applicant.
- Cannot approve an already approved or rejected application.
- Cannot approve an application if the pet has already been adopted by another applicant.

---

### Flow 2: Veterinary Appointment & Vaccination Workflow

```text
Pet Owner (Adopter)
     ↓
Select owned/adopted pet (Validated: pet must be owned by adopter)
     ↓
Book veterinary appointment (Date, Vet ID, Reason → Status: BOOKED)
     ↓
Veterinarian views clinic appointments
     ↓
Veterinarian marks appointment as COMPLETED
     ↓
Veterinarian records medical visit (Diagnosis, Treatment/Notes)
     ↓
Veterinarian records vaccination (Vaccine name, date, booster due date)
     ↓
Next vaccination date is calculated / stored (Auto 12-month booster or custom)
     ↓
Pet's vaccination history & clinic-wide upcoming due dates displayed
```

**Enforced Business Rules & Validations:**
- Only adopted/owned pets can be booked for owner veterinary appointments.
- An appointment marked `COMPLETED` cannot be completed again.
- Next vaccination date must be strictly after the vaccination date.
- Appointment and visit dates are validated against the `YYYY-MM-DD` standard.

---

## 4. Preloaded Sample Data

The application starts pre-seeded with:
- **Shelter Staff**: `S001` - Sarah (Adoptions Department)
- **Adopter / Pet Owner**: `A001` - John
- **Veterinarian**: `V001` - Dr. Kumar (Small Animal Internal Medicine, License: VET-9824)
- **Available Pets**:
  - `P001` - Bruno (Dog, Labrador, 3 years, Male, AVAILABLE)
  - `P002` - Luna (Cat, Persian, 2 years, Female, AVAILABLE)
  - `P003` - Max (Dog, Beagle, 4 years, Male, AVAILABLE)

---

## 5. Compilation & Running

### Using Command Line (PowerShell / Command Prompt):

1. **Compile**:
   ```bash
   javac -d bin -sourcepath src src/com/petcare/Main.java src/com/petcare/model/*.java src/com/petcare/service/*.java src/com/petcare/exception/*.java src/com/petcare/ui/*.java src/com/petcare/util/*.java
   ```

2. **Run**:
   ```bash
   java -cp bin com.petcare.Main
   ```

### Using Windows Batch Script:
Double click or execute `run.bat` in the project root:
```cmd
run.bat
```
