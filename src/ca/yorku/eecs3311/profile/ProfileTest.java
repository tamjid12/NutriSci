package ca.yorku.eecs3311.profile;
import java.util.Scanner;

public class ProfileTest {
    public static void main(String[] args) {
        ProfileUI ui = new ProfileUI();
        Scanner scanner = new Scanner(System.in);

        String answer;
        do {
            ui.clickCreateProfile();
            System.out.print("Add another profile? (yes/no): ");
            answer = scanner.nextLine();
        } while (answer.equalsIgnoreCase("yes"));

        // Show all profiles at the end
        ui.showAllProfiles();
    }
}
