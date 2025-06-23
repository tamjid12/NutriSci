package ca.yorku.eecs3311.profile;
import java.util.Scanner;

/**
 * A simple test class to interact with the Profile UI from the command line.
 * It simulates profile creation and displays all stored profiles.
 *
 * This test mimics user interaction by repeatedly allowing creation of new profiles
 * and displaying all profiles at the end.
 */
public class ProfileTest {
    public static void main(String[] args) {
        ProfileUI ui = new ProfileUI();
        Scanner scanner = new Scanner(System.in);

        String answer;
        // Loop to allow creation of multiple profiles
        do {
            ui.clickCreateProfile();
            System.out.print("Add another profile? (yes/no): ");
            answer = scanner.nextLine();
        } while (answer.equalsIgnoreCase("yes"));

        // Show all profiles at the end
        ui.showAllProfiles();
    }
}
