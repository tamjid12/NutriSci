package ca.yorku.eecs3311.meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MealEntryTest {
    public static void main(String[] args) {
        String profile = "TestUser";              // make sure this profile exists in your DB
        MealType type  = MealType.BREAKFAST;
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(8, 30);

        // 1) Create a new entry with two items
        MealEntry entry = new MealEntry(profile, type, date, time);
        entry.setItems(List.of(
                new MealItem(0, "Eggs", 2.0),
                new MealItem(0, "Bread", 1.0)
        ));

        // 2) Save it
        MealLogController ctrl = new MealLogController();
        boolean saved = ctrl.saveMeal(entry);
        System.out.println("Saved? " + saved);

        // 3) Load today's entries for this profile
        List<MealEntry> entries = ctrl.getMealsForUserOnDate(profile, date);
        System.out.println("Entries for " + profile + " on " + date + ":");
        for (MealEntry e : entries) {
            System.out.println("  " + e);
            // and list each item:
            for (MealItem item : e.getItems()) {
                System.out.println("    - " + item);
            }
        }
    }
}
