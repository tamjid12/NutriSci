package ca.yorku.eecs3311.meal;

import ca.yorku.eecs3311.profile.ProfileController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MealLogTest {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ProfileController profCtrl = new ProfileController();
        MealLogController ctrl = new MealLogController();

        System.out.print("Enter your profile name: ");
        String user = scan.nextLine().trim();

        System.out.print("Food item: ");
        String food = scan.nextLine().trim();

        System.out.print("Quantity: ");
        double qty = Double.parseDouble(scan.nextLine().trim());

        System.out.print("Meal type (BREAKFAST/LUNCH/DINNER/SNACK): ");
        MealType type = MealType.valueOf(scan.nextLine().toUpperCase().trim());

        MealLog log = new MealLog(user, type, LocalDateTime.now(), food, qty);
        boolean ok = ctrl.saveMeal(log);
        System.out.println("Saved? " + ok);

        System.out.println("\nAll meals for " + user + ":");
        List<MealLog> meals = ctrl.getMealsForUser(user);
        meals.forEach(System.out::println);
    }
}
