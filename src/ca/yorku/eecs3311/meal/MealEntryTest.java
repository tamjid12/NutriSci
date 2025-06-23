package ca.yorku.eecs3311.meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based test class to manually input and validate a MealEntry.
 * This class simulates the behavior of the Meal Logging UI for testing purposes
 * without Swing. It allows a user to input a meal's profile name, meal type,
 * date, time, and ingredient list, validates it, and saves it to the database.
 */

public class MealEntryTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Read profile name
        System.out.print("Enter profile name: ");
        String profile = scanner.nextLine().trim();

        // 2. Read meal type
        MealType type = null;
        while (type == null) {
            System.out.print("Enter meal type (breakfast, lunch, dinner, snack): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                type = MealType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Please enter breakfast, lunch, dinner, or snack.");
            }
        }

        // 3. Read date
        LocalDate date = null;
        while (date == null) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("Invalid format. Please use YYYY-MM-DD (e.g. 2025-06-19).");
            }
        }

        // 4. Read time
        LocalTime time = null;
        while (time == null) {
            System.out.print("Enter time (24-hour HH:MM): ");
            String input = scanner.nextLine().trim();
            try {
                time = LocalTime.parse(input);
            } catch (Exception e) {
                System.out.println("Invalid format. Please use HH:MM in 24-hour format (e.g. 08:30).");
            }
        }

        // 5. Read ingredients
        List<MealItem> items = new ArrayList<>();
        while (true) {
            System.out.print("Enter food name (blank to finish entry): ");
            String food = scanner.nextLine().trim();
            if (food.isEmpty()) break;
            System.out.print("Enter quantity in grams: ");
            double qty = Double.parseDouble(scanner.nextLine().trim());
            items.add(new MealItem(0, food, qty));
        }
        if (items.isEmpty()) {
            System.out.println("No ingredients entered. Exiting.");
            return;
        }

        // 6. Build Meal entry
        MealEntry entry = new MealEntry(profile, type, date, time);
        entry.setItems(items);

        MealLogController ctrl = new MealLogController();

        // 7. Validation for only one entry per meal per date
        if (type != MealType.SNACK) {
            List<MealEntry> existing = ctrl.getMealsForUserOnDate(profile, date);
            boolean already = false;
            for (MealEntry me : existing) {
                if (me.getMealType() == type) {
                    already = true;
                    break;
                }
            }
            if (already) {
                System.out.println(type.name().toLowerCase()
                        + " already entered for " + date + ".");
                return;
            }
        }

        // 8. save
        boolean saved = ctrl.saveMeal(entry);
        System.out.println("Save result: " + saved);

        // 9. List all entries back
        System.out.println("Entries for " + profile + " on " + date + ":");
        List<MealEntry> entries = ctrl.getMealsForUserOnDate(profile, date);
        for (MealEntry e : entries) {
            System.out.println("  " + e);
            for (MealItem it : e.getItems()) {
                System.out.println("    - " + it);
            }
        }
    }
}
