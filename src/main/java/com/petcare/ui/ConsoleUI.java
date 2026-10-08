package com.petcare.ui;

import com.petcare.exception.AdoptionException;
import com.petcare.exception.BusinessRuleException;
import com.petcare.exception.EntityNotFoundException;
import com.petcare.exception.PetNotFoundException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Adopter;
import com.petcare.model.AdoptionApplication;
import com.petcare.model.Appointment;
import com.petcare.model.Gender;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Pet;
import com.petcare.model.ShelterStaff;
import com.petcare.model.Vaccination;
import com.petcare.model.Veterinarian;
import com.petcare.service.AdoptionService;
import com.petcare.service.AppointmentService;
import com.petcare.service.MedicalService;
import com.petcare.service.PetService;
import com.petcare.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;

/**
 * Menu-driven console interface for Pet Adoption and Veterinary Clinic Management System.
 */
public class ConsoleUI {
    private final PetService petService;
    private final AdoptionService adoptionService;
    private final AppointmentService appointmentService;
    private final MedicalService medicalService;
    private final UserService userService;
    private final Scanner scanner;
    private boolean isInputClosed = false;

    public ConsoleUI(PetService petService, AdoptionService adoptionService,
                     AppointmentService appointmentService, MedicalService medicalService,
                     UserService userService) {
        this.petService = petService;
        this.adoptionService = adoptionService;
        this.appointmentService = appointmentService;
        this.medicalService = medicalService;
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running && !isInputClosed) {
            printMainMenu();
            String choice = prompt("Enter choice: ").trim();
            if (isInputClosed) {
                break;
            }
            if (choice.isEmpty()) {
                continue;
            }
            switch (choice) {
                case "1":
                    shelterStaffMenu();
                    break;
                case "2":
                    adopterMenu();
                    break;
                case "3":
                    veterinarianMenu();
                    break;
                case "4":
                    System.out.println("\nThank you for using Pet Adoption & Veterinary System. Goodbye!");
                    running = false;
                    break;
                default:
                    printError("Invalid option. Please enter a choice between 1 and 4.");
            }
        }
    }

    // ==========================================
    // MAIN MENU & SUB-MENUS
    // ==========================================

    private void printMainMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println(" PET ADOPTION & VETERINARY SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Shelter Staff");
        System.out.println("2. Adopter / Pet Owner");
        System.out.println("3. Veterinarian");
        System.out.println("4. Exit");
        System.out.println();
    }

    // ------------------------------------------
    // FLOW 1: SHELTER STAFF MENU
    // ------------------------------------------

    private void shelterStaffMenu() {
        List<ShelterStaff> list = userService.getAllShelterStaff();
        if (list.isEmpty()) {
            printError("No shelter staff members registered.");
            return;
        }
        ShelterStaff staff = list.get(0);

        boolean inMenu = true;
        while (inMenu && !isInputClosed) {
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println(" SHELTER STAFF MENU - Logged in as: " + staff.getName() + " (" + staff.getId() + ")");
            System.out.println("----------------------------------------");
            System.out.println("1. Add Pet");
            System.out.println("2. View All Pets");
            System.out.println("3. View Adoption Applications");
            System.out.println("4. Approve Application");
            System.out.println("5. Reject Application");
            System.out.println("6. Process Next Application in Queue (FIFO)");
            System.out.println("7. Back");
            System.out.println();

            String choice = prompt("Enter choice: ").trim();
            if (isInputClosed) {
                break;
            }
            if (choice.isEmpty()) {
                continue;
            }
            switch (choice) {
                case "1":
                    handleAddPet();
                    break;
                case "2":
                    handleViewAllPets();
                    break;
                case "3":
                    handleViewApplications();
                    break;
                case "4":
                    handleApproveApplication();
                    break;
                case "5":
                    handleRejectApplication();
                    break;
                case "6":
                    handleProcessQueue();
                    break;
                case "7":
                    inMenu = false;
                    break;
                default:
                    printError("Invalid choice. Please enter 1-7.");
            }
        }
    }

    // ------------------------------------------
    // FLOW 1 & 2: ADOPTER / PET OWNER MENU
    // ------------------------------------------

    private void adopterMenu() {
        List<Adopter> list = userService.getAllAdopters();
        if (list.isEmpty()) {
            printError("No adopters registered.");
            return;
        }
        Adopter adopter = list.get(0);

        boolean inMenu = true;
        while (inMenu && !isInputClosed) {
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println(" ADOPTER MENU - Logged in as: " + adopter.getName() + " (" + adopter.getId() + ")");
            System.out.println("----------------------------------------");
            System.out.println("1. View Available Pets");
            System.out.println("2. Filter Pets");
            System.out.println("3. Apply for Adoption");
            System.out.println("4. View My Applications");
            System.out.println("5. Book Vet Appointment");
            System.out.println("6. View My Pet's Vaccinations");
            System.out.println("7. Back");
            System.out.println();

            String choice = prompt("Enter choice: ").trim();
            if (isInputClosed) {
                break;
            }
            if (choice.isEmpty()) {
                continue;
            }
            switch (choice) {
                case "1":
                    handleViewAvailablePets();
                    break;
                case "2":
                    handleFilterPets();
                    break;
                case "3":
                    handleApplyForAdoption(adopter);
                    break;
                case "4":
                    handleViewMyApplications(adopter);
                    break;
                case "5":
                    handleBookAppointment(adopter);
                    break;
                case "6":
                    handleViewMyPetVaccinations(adopter);
                    break;
                case "7":
                    inMenu = false;
                    break;
                default:
                    printError("Invalid choice. Please enter 1-7.");
            }
        }
    }

    // ------------------------------------------
    // FLOW 2: VETERINARIAN MENU
    // ------------------------------------------

    private void veterinarianMenu() {
        List<Veterinarian> list = userService.getAllVeterinarians();
        if (list.isEmpty()) {
            printError("No veterinarians registered.");
            return;
        }
        Veterinarian vet = list.get(0);

        boolean inMenu = true;
        while (inMenu && !isInputClosed) {
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println(" VETERINARIAN MENU - Logged in as: " + vet.getName() + " (" + vet.getId() + ")");
            System.out.println("----------------------------------------");
            System.out.println("1. View Appointments");
            System.out.println("2. Complete Appointment");
            System.out.println("3. Add Medical Record");
            System.out.println("4. Record Vaccination");
            System.out.println("5. View Pet Medical History");
            System.out.println("6. View Vaccination History");
            System.out.println("7. Back");
            System.out.println();

            String choice = prompt("Enter choice: ").trim();
            if (isInputClosed) {
                break;
            }
            if (choice.isEmpty()) {
                continue;
            }
            switch (choice) {
                case "1":
                    handleViewAppointments(vet);
                    break;
                case "2":
                    handleCompleteAppointment();
                    break;
                case "3":
                    handleAddMedicalRecord(vet);
                    break;
                case "4":
                    handleRecordVaccination();
                    break;
                case "5":
                    handleViewPetMedicalHistory();
                    break;
                case "6":
                    handleViewVaccinationHistory();
                    break;
                case "7":
                    inMenu = false;
                    break;
                default:
                    printError("Invalid choice. Please enter 1-7.");
            }
        }
    }

    // ==========================================
    // FLOW 1 HANDLERS (SHELTER STAFF & ADOPTER)
    // ==========================================

    private void handleAddPet() {
        System.out.println("\n--- Add New Pet to Shelter ---");
        try {
            String name = prompt("Enter Pet Name: ");
            String species = prompt("Enter Species (e.g. Dog, Cat): ");
            String breed = prompt("Enter Breed (e.g. Labrador, Persian): ");
            String ageStr = prompt("Enter Age (in years): ");
            int age;
            try {
                age = Integer.parseInt(ageStr.trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("Age must be a valid non-negative integer.");
            }
            String genderStr = prompt("Enter Gender (M/F or Male/Female): ");
            Gender gender = Gender.fromString(genderStr);

            Pet created = petService.addPet(name, species, breed, age, gender);
            printSuccess("Pet registered successfully!");
            System.out.println("Created: " + created);
        } catch (ValidationException e) {
            printError(e.getMessage());
        }
    }

    private void handleViewAllPets() {
        System.out.println("\n--- All Pets in System ---");
        List<Pet> pets = petService.getAllPets();
        if (pets.isEmpty()) {
            System.out.println("No pets registered in the system.");
            return;
        }
        printPetTable(pets);
    }

    private void handleViewAvailablePets() {
        System.out.println("\n--- Available Pets for Adoption ---");
        List<Pet> pets = petService.getAvailablePets();
        if (pets.isEmpty()) {
            System.out.println("No pets currently available for adoption.");
            return;
        }
        printPetTable(pets);
    }

    private void handleFilterPets() {
        System.out.println("\n--- Filter Available Pets ---");
        String species = prompt("Filter by Species (press Enter to skip): ").trim();
        String breed = prompt("Filter by Breed (press Enter to skip): ").trim();

        List<Pet> filtered = petService.filterPets(
                species.isEmpty() ? null : species,
                breed.isEmpty() ? null : breed
        );

        if (filtered.isEmpty()) {
            System.out.println("No available pets match the specified filter.");
            return;
        }
        printPetTable(filtered);
    }

    private void handleApplyForAdoption(Adopter adopter) {
        System.out.println("\n--- Submit Adoption Application ---");
        handleViewAvailablePets();
        try {
            String petId = prompt("Enter Pet ID to adopt: ").trim().toUpperCase();
            String reason = prompt("Enter reason for adoption: ");

            AdoptionApplication app = adoptionService.submitApplication(petId, adopter.getId(), reason);
            printSuccess("Adoption application submitted successfully!");
            System.out.println("Application ID: " + app.getApplicationId() + " (Status: " + app.getStatus() + ")");
        } catch (AdoptionException | EntityNotFoundException | ValidationException | BusinessRuleException e) {
            printError(e.getMessage());
        }
    }

    private void handleViewApplications() {
        System.out.println("\n--- Adoption Applications ---");
        List<AdoptionApplication> apps = adoptionService.getAllApplications();
        if (apps.isEmpty()) {
            System.out.println("No adoption applications have been submitted.");
            return;
        }
        printApplicationTable(apps);
    }

    private void handleViewMyApplications(Adopter adopter) {
        System.out.println("\n--- My Adoption Applications (" + adopter.getName() + ") ---");
        List<AdoptionApplication> apps = adoptionService.getApplicationsByAdopter(adopter.getId());
        if (apps.isEmpty()) {
            System.out.println("You have not submitted any adoption applications yet.");
            return;
        }
        printApplicationTable(apps);
    }

    private void handleApproveApplication() {
        System.out.println("\n--- Approve Adoption Application ---");
        List<AdoptionApplication> pending = adoptionService.getPendingApplications();
        if (pending.isEmpty()) {
            System.out.println("No pending applications awaiting approval.");
            return;
        }
        printApplicationTable(pending);

        try {
            String appId = prompt("Enter Application ID to approve: ").trim().toUpperCase();
            String notes = prompt("Enter approval notes / remarks (optional): ");

            AdoptionApplication approved = adoptionService.approveApplication(appId, notes);
            printSuccess("Application " + approved.getApplicationId() + " APPROVED!");
            Pet pet = petService.getPetById(approved.getPetId());
            System.out.println(">> Pet " + pet.getPetId() + " (" + pet.getName() + ") status is now: " + pet.getAdoptionStatus());
            System.out.println(">> Registered Owner: " + pet.getOwnerId());
        } catch (AdoptionException | EntityNotFoundException | BusinessRuleException | ValidationException e) {
            printError(e.getMessage());
        }
    }

    private void handleRejectApplication() {
        System.out.println("\n--- Reject Adoption Application ---");
        List<AdoptionApplication> pending = adoptionService.getPendingApplications();
        if (pending.isEmpty()) {
            System.out.println("No pending applications.");
            return;
        }
        printApplicationTable(pending);

        try {
            String appId = prompt("Enter Application ID to reject: ").trim().toUpperCase();
            String reason = prompt("Enter rejection reason / remarks: ");

            AdoptionApplication rejected = adoptionService.rejectApplication(appId, reason);
            printSuccess("Application " + rejected.getApplicationId() + " REJECTED.");
            Pet pet = petService.getPetById(rejected.getPetId());
            System.out.println(">> Pet " + pet.getPetId() + " (" + pet.getName() + ") remains: " + pet.getAdoptionStatus());
        } catch (AdoptionException | EntityNotFoundException | BusinessRuleException | ValidationException e) {
            printError(e.getMessage());
        }
    }

    private void handleProcessQueue() {
        System.out.println("\n--- Process Next Application from Queue (FIFO) ---");
        Queue<AdoptionApplication> queue = adoptionService.getApplicationQueue();
        if (queue.isEmpty()) {
            System.out.println("Queue is empty. No pending applications.");
            return;
        }
        AdoptionApplication next = queue.peek();
        System.out.println("Next in FIFO queue: Application " + next.getApplicationId() +
                " for Pet " + next.getPetId() + " by Adopter " + next.getAdopterId());
        String action = prompt("Action: Approve (A) or Reject (R)? ").trim().toUpperCase();
        boolean approve = !action.startsWith("R");
        String notes = prompt("Enter review notes: ");

        try {
            AdoptionApplication processed = adoptionService.processNextApplicationInQueue(approve, notes);
            printSuccess("Processed Application " + processed.getApplicationId() +
                    " -> " + processed.getStatus() + " via Queue!");
        } catch (AdoptionException | EntityNotFoundException | BusinessRuleException | ValidationException e) {
            printError(e.getMessage());
        }
    }

    // ==========================================
    // FLOW 2 HANDLERS (VETERINARY & VACCINATIONS)
    // ==========================================

    private void handleBookAppointment(Adopter adopter) {
        System.out.println("\n--- Book Veterinary Appointment ---");
        List<Pet> ownedPets = petService.getPetsByOwner(adopter.getId());
        if (ownedPets.isEmpty()) {
            printError("You do not have any adopted pets registered. You can only book appointments for owned pets.");
            return;
        }

        System.out.println("Your Owned Pets:");
        printPetTable(ownedPets);

        try {
            String petId = prompt("Enter Pet ID: ").trim().toUpperCase();

            // Display available veterinarians
            List<Veterinarian> vets = userService.getAllVeterinarians();
            System.out.println("\nAvailable Veterinarians:");
            for (Veterinarian v : vets) {
                System.out.printf("  %s - %s (%s)%n", v.getId(), v.getName(), v.getSpecialization());
            }

            String vetId = prompt("Enter Veterinarian ID (Press Enter for " + vets.get(0).getId() + "): ").trim().toUpperCase();
            if (vetId.isEmpty()) {
                vetId = vets.get(0).getId();
            }

            String dateInput = prompt("Enter Appointment Date [YYYY-MM-DD] (Press Enter for Today): ").trim();
            LocalDate appDate;
            if (dateInput.isEmpty()) {
                appDate = LocalDate.now();
            } else {
                try {
                    appDate = LocalDate.parse(dateInput);
                } catch (DateTimeParseException e) {
                    throw new ValidationException("Invalid date format. Expected YYYY-MM-DD (e.g. 2026-10-15).");
                }
            }

            String reason = prompt("Enter Reason for Appointment: ");

            Appointment apt = appointmentService.bookAppointment(petId, adopter.getId(), vetId, appDate, reason);
            printSuccess("Appointment booked successfully!");
            System.out.println("Appointment ID: " + apt.getAppointmentId());
            System.out.println("Date: " + apt.getAppointmentDate() + " | Status: " + apt.getStatus());
        } catch (EntityNotFoundException | BusinessRuleException | ValidationException e) {
            printError(e.getMessage());
        }
    }

    private void handleViewMyPetVaccinations(Adopter adopter) {
        System.out.println("\n--- View Vaccination Records ---");
        List<Pet> ownedPets = petService.getPetsByOwner(adopter.getId());
        if (ownedPets.isEmpty()) {
            printError("You do not have any adopted pets registered.");
            return;
        }

        System.out.println("Your Owned Pets:");
        printPetTable(ownedPets);

        String petId = prompt("Enter Pet ID: ").trim().toUpperCase();
        try {
            Pet pet = petService.getPetById(petId);
            if (!adopter.ownsPet(pet.getPetId())) {
                printError("Pet " + petId + " does not belong to you.");
                return;
            }

            List<Vaccination> vacs = medicalService.getVaccinationHistoryByPet(pet.getPetId());
            if (vacs.isEmpty()) {
                System.out.println("No vaccination records found for " + pet.getName() + " (" + pet.getPetId() + ").");
                return;
            }
            printVaccinationTable(vacs);
        } catch (EntityNotFoundException e) {
            printError(e.getMessage());
        }
    }

    private void handleViewAppointments(Veterinarian vet) {
        System.out.println("\n--- Clinic Appointments ---");
        List<Appointment> allAppointments = appointmentService.getAllAppointments();
        if (allAppointments.isEmpty()) {
            System.out.println("No appointments scheduled in the clinic.");
            return;
        }
        printAppointmentTable(allAppointments);
    }

    private void handleCompleteAppointment() {
        System.out.println("\n--- Complete Appointment ---");
        List<Appointment> all = appointmentService.getAllAppointments();
        if (all.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        printAppointmentTable(all);

        try {
            String aptId = prompt("Enter Appointment ID to mark COMPLETED: ").trim().toUpperCase();
            Appointment completed = appointmentService.completeAppointment(aptId);
            printSuccess("Appointment " + completed.getAppointmentId() + " marked as COMPLETED!");
        } catch (EntityNotFoundException | BusinessRuleException e) {
            printError(e.getMessage());
        }
    }

    private void handleAddMedicalRecord(Veterinarian vet) {
        System.out.println("\n--- Record Medical Visit ---");
        try {
            String petId = prompt("Enter Pet ID: ").trim().toUpperCase();
            Pet pet = petService.getPetById(petId);
            System.out.println("Patient: " + pet.getName() + " (" + pet.getSpecies() + ", " + pet.getBreed() + ")");

            String dateInput = prompt("Enter Visit Date [YYYY-MM-DD] (Press Enter for Today): ").trim();
            LocalDate visitDate = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);

            String diagnosis = prompt("Enter Diagnosis: ");
            String treatment = prompt("Enter Treatment / Prescriptions / Notes: ");

            MedicalRecord rec = medicalService.addMedicalRecord(petId, vet.getId(), visitDate, diagnosis, treatment);
            printSuccess("Medical record saved successfully!");
            System.out.println("Record ID: " + rec.getRecordId());
            System.out.println("Details: " + rec);
        } catch (DateTimeParseException e) {
            printError("Invalid date format. Expected YYYY-MM-DD.");
        } catch (EntityNotFoundException | ValidationException e) {
            printError(e.getMessage());
        }
    }

    private void handleRecordVaccination() {
        System.out.println("\n--- Record Vaccination ---");
        try {
            String petId = prompt("Enter Pet ID: ").trim().toUpperCase();
            Pet pet = petService.getPetById(petId);
            System.out.println("Patient: " + pet.getName() + " (" + pet.getSpecies() + ")");

            String vaccineName = prompt("Enter Vaccine Name (e.g. Rabies, DHPP, FVRCP): ");
            String dateInput = prompt("Enter Vaccination Date [YYYY-MM-DD] (Press Enter for Today): ").trim();
            LocalDate vacDate = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);

            System.out.println("Select Next Due Date Mode:");
            System.out.println("1. Standard Annual Booster (12 months from administration)");
            System.out.println("2. Semi-Annual Booster (6 months)");
            System.out.println("3. Custom Date Input");
            String mode = prompt("Choice (1-3, default 1): ").trim();

            Vaccination vac;
            if ("2".equals(mode)) {
                vac = medicalService.recordVaccinationWithAutoDueDate(petId, vaccineName, vacDate, 6);
            } else if ("3".equals(mode)) {
                String dueInput = prompt("Enter Next Due Date [YYYY-MM-DD]: ").trim();
                LocalDate dueDate = LocalDate.parse(dueInput);
                vac = medicalService.recordVaccination(petId, vaccineName, vacDate, dueDate);
            } else {
                vac = medicalService.recordVaccinationWithAutoDueDate(petId, vaccineName, vacDate, 12);
            }

            printSuccess("Vaccination recorded successfully!");
            System.out.println("Vaccination ID: " + vac.getVaccinationId());
            System.out.println("Vaccine: " + vac.getVaccineName());
            System.out.println("Administered: " + vac.getVaccinationDate());
            System.out.println("Next Booster Due Date: " + vac.getNextDueDate());
        } catch (DateTimeParseException e) {
            printError("Invalid date format. Expected YYYY-MM-DD.");
        } catch (EntityNotFoundException | ValidationException | BusinessRuleException e) {
            printError(e.getMessage());
        }
    }

    private void handleViewPetMedicalHistory() {
        System.out.println("\n--- Pet Medical History ---");
        try {
            String petId = prompt("Enter Pet ID: ").trim().toUpperCase();
            Pet pet = petService.getPetById(petId);
            System.out.println("Medical records for " + pet.getName() + " (" + pet.getPetId() + "):");

            List<MedicalRecord> history = medicalService.getMedicalHistoryByPet(petId);
            if (history.isEmpty()) {
                System.out.println("No medical examination records found for this pet.");
                return;
            }
            printMedicalRecordTable(history);
        } catch (EntityNotFoundException e) {
            printError(e.getMessage());
        }
    }

    private void handleViewVaccinationHistory() {
        System.out.println("\n--- Vaccination History & Upcoming Due Dates ---");
        String petId = prompt("Enter Pet ID (or press Enter to view all clinic upcoming due dates): ").trim().toUpperCase();

        if (petId.isEmpty()) {
            System.out.println("\nUpcoming Vaccinations (Clinic-wide):");
            List<Vaccination> upcoming = medicalService.getUpcomingVaccinations(LocalDate.now());
            if (upcoming.isEmpty()) {
                System.out.println("No upcoming vaccinations scheduled.");
                return;
            }
            printVaccinationTable(upcoming);
        } else {
            try {
                Pet pet = petService.getPetById(petId);
                System.out.println("\nVaccination history for " + pet.getName() + " (" + pet.getPetId() + "):");
                List<Vaccination> history = medicalService.getVaccinationHistoryByPet(petId);
                if (history.isEmpty()) {
                    System.out.println("No vaccination records found for this pet.");
                    return;
                }
                printVaccinationTable(history);
            } catch (EntityNotFoundException e) {
                printError(e.getMessage());
            }
        }
    }

    // ==========================================
    // FORMATTED TABLE PRINTERS
    // ==========================================

    private void printPetTable(List<Pet> pets) {
        String format = "%-6s | %-10s | %-8s | %-12s | %-5s | %-7s | %-11s | %-8s%n";
        String line = "--------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf(format, "ID", "Name", "Species", "Breed", "Age", "Gender", "Status", "Owner");
        System.out.println(line);
        for (Pet p : pets) {
            System.out.printf(format,
                    p.getPetId(),
                    p.getName(),
                    p.getSpecies(),
                    p.getBreed(),
                    p.getAge() + "y",
                    p.getGender(),
                    p.getAdoptionStatus(),
                    p.getOwnerId() != null ? p.getOwnerId() : "-");
        }
        System.out.println(line);
    }

    private void printApplicationTable(List<AdoptionApplication> apps) {
        String format = "%-8s | %-6s | %-8s | %-11s | %-10s | %-25s | %-20s%n";
        String line = "-------------------------------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf(format, "App ID", "Pet", "Adopter", "Date", "Status", "Reason", "Notes");
        System.out.println(line);
        for (AdoptionApplication a : apps) {
            String notes = a.getReviewNotes() != null ? a.getReviewNotes() : "-";
            String reason = a.getReason().length() > 25 ? a.getReason().substring(0, 22) + "..." : a.getReason();
            System.out.printf(format,
                    a.getApplicationId(),
                    a.getPetId(),
                    a.getAdopterId(),
                    a.getApplicationDate(),
                    a.getStatus(),
                    reason,
                    notes);
        }
        System.out.println(line);
    }

    private void printAppointmentTable(List<Appointment> appointments) {
        String format = "%-8s | %-11s | %-6s | %-8s | %-8s | %-11s | %-25s%n";
        String line = "---------------------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf(format, "Apt ID", "Date", "Pet", "Owner", "Vet", "Status", "Reason");
        System.out.println(line);
        for (Appointment a : appointments) {
            String reason = a.getReason().length() > 25 ? a.getReason().substring(0, 22) + "..." : a.getReason();
            System.out.printf(format,
                    a.getAppointmentId(),
                    a.getAppointmentDate(),
                    a.getPetId(),
                    a.getOwnerId(),
                    a.getVeterinarianId(),
                    a.getStatus(),
                    reason);
        }
        System.out.println(line);
    }

    private void printMedicalRecordTable(List<MedicalRecord> records) {
        String format = "%-8s | %-11s | %-6s | %-8s | %-22s | %-25s%n";
        String line = "---------------------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf(format, "Rec ID", "Visit Date", "Pet", "Vet", "Diagnosis", "Treatment/Notes");
        System.out.println(line);
        for (MedicalRecord r : records) {
            System.out.printf(format,
                    r.getRecordId(),
                    r.getVisitDate(),
                    r.getPetId(),
                    r.getVeterinarianId(),
                    r.getDiagnosis(),
                    r.getTreatmentNotes());
        }
        System.out.println(line);
    }

    private void printVaccinationTable(List<Vaccination> list) {
        String format = "%-8s | %-6s | %-15s | %-12s | %-12s%n";
        String line = "----------------------------------------------------------------";
        System.out.println(line);
        System.out.printf(format, "Vac ID", "Pet", "Vaccine Name", "Administered", "Next Due");
        System.out.println(line);
        for (Vaccination v : list) {
            System.out.printf(format,
                    v.getVaccinationId(),
                    v.getPetId(),
                    v.getVaccineName(),
                    v.getVaccinationDate(),
                    v.getNextDueDate());
        }
        System.out.println(line);
    }

    // ==========================================
    // UTILITY INPUT & OUTPUT HELPERS
    // ==========================================

    private String prompt(String message) {
        System.out.print(message);
        System.out.flush();
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException | IllegalStateException e) {
            isInputClosed = true;
            System.out.println();
            return "";
        }
    }

    private void printSuccess(String message) {
        System.out.println("\n[SUCCESS] " + message);
    }

    private void printError(String message) {
        System.out.println("\n[ERROR] " + message);
    }
}
