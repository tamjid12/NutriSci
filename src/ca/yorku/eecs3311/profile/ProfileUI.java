package ca.yorku.eecs3311.profile;

import java.util.Scanner;

public class ProfileUI {
    private ProfileController controller = new ProfileController();

    public void clickCreateProfile() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        // Validate sex input
        String sex;
        while (true) {
            System.out.print("Enter your sex (Male/Female): ");
            sex = scanner.nextLine().trim();
            if (sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female")) {
                sex = sex.substring(0, 1).toUpperCase() + sex.substring(1).toLowerCase();
                break;
            } else {
                System.out.println("Invalid input. Please enter 'Male' or 'Female'.");
            }
        }

        // Validate age input
        int age = 0;
        while (true) {
            System.out.print("Enter your age: ");
            String ageStr = scanner.nextLine();
            try {
                age = Integer.parseInt(ageStr.trim());
                if (age > 0 && age < 150) { // reasonable bounds
                    break;
                } else {
                    System.out.println("Please enter a valid positive age (1-149).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value for age.");
            }
        }

        UserProfile profile = UserProfileFactory.createUserProfile(name, sex, age);
        boolean success = controller.saveProfile(profile);

        if (success) {
            displaySuccessMessage();
        }
    }

    public void showAllProfiles() {
        System.out.println("All saved profiles:");
        for (UserProfile profile : controller.getProfiles()) {
            System.out.println(profile.getName() + " | " + profile.getSex() + " | " + profile.getAge());
        }
    }

    private void displaySuccessMessage() {
        System.out.println("Profile created and saved successfully!");
    }
}
