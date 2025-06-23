package ca.yorku.eecs3311.profile;

import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Command-line UI class for creating and displaying user profiles.
 * Handles user input for fields such as name, sex, DOB, height, weight, and units.
 * Delegates profile persistence to the ProfileController
 */
public class ProfileUI {
    private ProfileController controller = new ProfileController();

    /**
     * Prompts the user for all profile fields, creates a UserProfile,
     * and saves it via the controller/DAO.
     */
    public void clickCreateProfile() {
        Scanner scanner = new Scanner(System.in);

        // 1) Name
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        // 2) Sex
        String sex;
        while (true) {
            System.out.print("Enter your sex (Male/Female): ");
            sex = scanner.nextLine().trim();
            if (sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female")) {
                sex = sex.substring(0,1).toUpperCase() + sex.substring(1).toLowerCase();
                break;
            }
            System.out.println("Invalid input. Please enter 'Male' or 'Female'.");
        }

        // 3) Date of Birth
        LocalDate dob;
        while (true) {
            System.out.print("Enter your date of birth (YYYY-MM-DD): ");
            String dobStr = scanner.nextLine().trim();
            try {
                dob = LocalDate.parse(dobStr);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use YYYY-MM-DD.");
            }
        }

        // 4) Height
        double height;
        while (true) {
            System.out.print("Enter your height: ");
            String h = scanner.nextLine().trim();
            try {
                height = Double.parseDouble(h);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }

        // 5) Weight
        double weight;
        while (true) {
            System.out.print("Enter your weight: ");
            String w = scanner.nextLine().trim();
            try {
                weight = Double.parseDouble(w);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }

        // 6) Units
        UnitSystem units;
        while (true) {
            System.out.print("Enter units (Metric/Imperial): ");
            String u = scanner.nextLine().trim();
            if (u.equalsIgnoreCase("metric")) {
                units = UnitSystem.METRIC;
                break;
            } else if (u.equalsIgnoreCase("imperial")) {
                units = UnitSystem.IMPERIAL;
                break;
            }
            System.out.println("Invalid. Please enter 'Metric' or 'Imperial'.");
        }

        // Create & save
        UserProfile profile = UserProfileFactory.createUserProfile(
                name, sex, dob, height, weight, units
        );
        boolean success = controller.saveProfile(profile);
        if (success) {
            displaySuccessMessage();
        } else {
            System.out.println("Failed to save profile.");
        }
    }

    /**
     * Retrieves all saved profiles and prints them.
     */
    public void showAllProfiles() {
        System.out.println("\nAll saved profiles:");
        List<UserProfile> list = controller.getProfiles();
        if (list.isEmpty()) {
            System.out.println("  (no profiles found)");
        }
        for (UserProfile p : list) {
            // Relies on your UserProfile.toString()
            System.out.println("  " + p);
        }
    }

    private void displaySuccessMessage() {
        System.out.println("Profile created and saved successfully!");
    }
}
