package ca.yorku.eecs3311.nutrient;

import ca.yorku.eecs3311.meal.MealItem;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A simple console-based test class for verifying nutrient calculations
 * based on user-provided food input and quantity in grams.
 *
 * This test demonstrates how the NutrientCalculator can be used
 * to compute calorie, protein, fat, and carbohydrate information for
 * a given food item retrieved from the CNF database.
 * The Nutrient Calculator in Java swing has more information, This is just a test on the console.
 */
public class NutrientCalculatorTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Read food description from user
        System.out.print("Enter food description (exact match): ");
        String food = scanner.nextLine().trim();

        // 2. Read quantity in grams
        System.out.print("Enter quantity in grams: ");
        double qty;
        try {
            qty = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Exiting.");
            scanner.close();
            return;
        }

        // 3. Build list and calculate
        List<MealItem> items = List.of(new MealItem(0, food, qty));
        NutrientCalculator calc = new NutrientCalculator();
        Map<String, NutrientInfo> nuts;
        try {
            nuts = calc.calcForItemsWithUnits(items);
        } catch (Exception e) {
            System.err.println("Error during calculation: " + e.getMessage());
            e.printStackTrace();
            scanner.close();
            System.exit(2);
            return;
        }

        // 4. Display four basic nutrients
        Map<String, String> displayNames = Map.of(
                "KCAL", "Calories",
                "PROT", "Protein",
                "FAT",  "Fat",
                "CARB", "Carbohydrates"
        );

        // 5) Output Summary
        System.out.printf("=== Nutrient totals for %.2f g of %s ===%n", qty, food);
        boolean allAvailable = true;
        for (var entry : displayNames.entrySet()) {
            String sym  = entry.getKey();
            String name = entry.getValue();
            NutrientInfo info = nuts.get(sym);
            if (info == null) {
                System.out.printf("%s: N/A%n", name);
                allAvailable = false;
            } else {
                System.out.printf("%s: %.2f %s%n", name, info.getAmount(), info.getUnit());
            }
        }


        if (allAvailable) {
            System.out.println("All basic nutrients calculated successfully.");
            System.exit(0);
        } else {
            System.out.println("Some nutrients were missing.");
            System.exit(1);
        }
    }
}
