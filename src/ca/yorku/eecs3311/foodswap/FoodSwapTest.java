package ca.yorku.eecs3311.foodswap;

import java.util.*;

public class FoodSwapTest {

    // Class to represent detailed meal info
    static class MealInfo {
        String name;
        double grams;
        double calories;
        double protein;
        double sugar;
        double fiber;

        MealInfo(String name, double grams, double cal, double protein, double sugar, double fiber) {
            this.name = name;
            this.grams = grams;
            this.calories = cal;
            this.protein = protein;
            this.sugar = sugar;
            this.fiber = fiber;
        }

        @Override
        public String toString() {
            return String.format("%s (%.1fg): %.0f cal, %.1fg protein, %.1fg sugar, %.1fg fiber",
                    name, grams, calories, protein, sugar, fiber);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Create a more complete original meal
        List<MealInfo> originalMeal = new ArrayList<>(List.of(
                new MealInfo("White Bread", 100.0, 265, 9.0, 5.0, 2.7),
                new MealInfo("Bacon", 50.0, 270, 20.0, 0.5, 0.0),
                new MealInfo("Cheddar Cheese", 30.0, 120, 7.0, 0.2, 0.0),
                new MealInfo("Fried Egg", 60.0, 90, 6.0, 0.3, 0.0),
                new MealInfo("Apple Juice", 250.0, 120, 0.5, 25.0, 0.5)
        ));

        // Store the working version of the meal (to apply swaps)
        List<MealInfo> workingMeal = new ArrayList<>(originalMeal);

        boolean running = true;

        while (running) {
            // Step 2: Display current meal
            System.out.println("\nCurrent Meal:");
            for (MealInfo item : workingMeal) {
                System.out.println("- " + item);
            }

            // Step 3: Ask user for goal
            System.out.println("\nChoose a swap goal:");
            System.out.println("1. Reduce Calories");
            System.out.println("2. Increase Protein");
            System.out.println("3. Reduce Sugar");
            System.out.println("4. Increase Fiber");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> workingMeal = applySwap(workingMeal, "calories");
                case 2 -> workingMeal = applySwap(workingMeal, "protein");
                case 3 -> workingMeal = applySwap(workingMeal, "sugar");
                case 4 -> workingMeal = applySwap(workingMeal, "fiber");
                case 0 -> {
                    System.out.println("Exiting... Final meal:");
                    for (MealInfo item : workingMeal) {
                        System.out.println("- " + item);
                    }
                    running = false;
                }
                default -> System.out.println("Invalid input.");
            }
        }

        scanner.close();
    }

    // Mock swap logic based on goal
    private static List<MealInfo> applySwap(List<MealInfo> meal, String goal) {
        List<MealInfo> updated = new ArrayList<>();

        for (MealInfo item : meal) {
            switch (goal) {
                case "calories" -> {
                    if (item.name.equalsIgnoreCase("Bacon")) {
                        updated.add(new MealInfo("Turkey Bacon", item.grams, 180, 19.0, 0.3, 0.0));
                    } else if (item.name.equalsIgnoreCase("Cheddar Cheese")) {
                        updated.add(new MealInfo("Low-Fat Cheese", item.grams, 80, 7.0, 0.1, 0.0));
                    } else {
                        updated.add(item);
                    }
                }
                case "protein" -> {
                    if (item.name.equalsIgnoreCase("Apple Juice")) {
                        updated.add(new MealInfo("Soy Milk", item.grams, 100, 7.0, 5.0, 1.0));
                    } else {
                        updated.add(item);
                    }
                }
                case "sugar" -> {
                    if (item.name.equalsIgnoreCase("Apple Juice")) {
                        updated.add(new MealInfo("Unsweetened Almond Milk", item.grams, 30, 1.0, 0.0, 0.5));
                    } else {
                        updated.add(item);
                    }
                }
                case "fiber" -> {
                    if (item.name.equalsIgnoreCase("White Bread")) {
                        updated.add(new MealInfo("Whole Grain Bread", item.grams, 240, 10.0, 3.0, 6.0));
                    } else {
                        updated.add(item);
                    }
                }
                default -> updated.add(item);
            }
        }

        System.out.println("\nSwap applied for: " + goal.toUpperCase());
        return updated;
    }
}
