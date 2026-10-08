package com.petcare;

import com.petcare.service.AdoptionService;
import com.petcare.service.AdoptionServiceImpl;
import com.petcare.service.AppointmentService;
import com.petcare.service.AppointmentServiceImpl;
import com.petcare.service.MedicalService;
import com.petcare.service.MedicalServiceImpl;
import com.petcare.service.PetService;
import com.petcare.service.PetServiceImpl;
import com.petcare.service.UserService;
import com.petcare.ui.ConsoleUI;
import com.petcare.util.SampleDataLoader;

/**
 * Entry point for the Pet Adoption & Veterinary Clinic Management System.
 * Initializes services, seeds sample data, and starts the menu-driven console UI.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Initialize Service Layer (Pure In-Memory, Plain Java)
        UserService userService = new UserService();
        PetService petService = new PetServiceImpl();
        AdoptionService adoptionService = new AdoptionServiceImpl(petService, userService);
        AppointmentService appointmentService = new AppointmentServiceImpl(petService, userService);
        MedicalService medicalService = new MedicalServiceImpl(petService, userService);

        // 2. Seed Sample Data (Pets: Bruno, Luna, Max | Users: John, Sarah, Dr. Kumar)
        SampleDataLoader.loadSampleData(petService, userService);

        // 3. Welcome Banner
        System.out.println("===============================================================================");
        System.out.println("     WELCOME TO THE PET ADOPTION & VETERINARY CLINIC MANAGEMENT SYSTEM        ");
        System.out.println("===============================================================================");
        System.out.println("Initial Sample Records Loaded:");
        System.out.println("  * Shelter Staff : S001 - Sarah");
        System.out.println("  * Adopter       : A001 - John");
        System.out.println("  * Veterinarian  : V001 - Dr. Kumar");
        System.out.println("  * Pets          : P001 (Bruno - Dog), P002 (Luna - Cat), P003 (Max - Dog)");
        System.out.println("===============================================================================");

        // 4. Start Console Interface
        ConsoleUI ui = new ConsoleUI(petService, adoptionService, appointmentService, medicalService, userService);
        ui.start();
    }
}
