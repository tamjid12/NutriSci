package ca.yorku.eecs3311.foodswap;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FoodSwapTest {

    static class MealInfo {
        String name;
        double grams;
        double calories;
        double protein;

        MealInfo(String name, double grams, double cal, double protein) {
            this.name = name;
            this.grams = grams;
            this.calories = cal;
            this.protein = protein;
        }

        @Override
        public String toString() {
            return String.format("%s: Quantity: %.0f g | Calories: %.0f kCal | Protein: %.2f g",
                    name, grams, calories, protein);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<MealInfo> originalMeal = new ArrayList<>(List.of(
                new MealInfo("White Bread", 100.0, 265, 9.0),
                new MealInfo("Bacon", 50.0, 270, 20.0),
                new MealInfo("Cheddar Cheese", 30.0, 120, 7.0),
                new MealInfo("Fried Egg", 60.0, 90, 6.0),
                new MealInfo("Apple Juice", 250.0, 120, 0.5)
        ));

        List<MealInfo> workingMeal = new ArrayList<>(originalMeal);

        boolean running = true;

        while (running) {
            System.out.println("\nCurrent Meal:");
            for (MealInfo item : workingMeal) {
                System.out.println("- " + item);
            }

            System.out.println("\nChoose a swap goal:");
            System.out.println("1. Reduce Calories");
            System.out.println("2. Increase Protein");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> workingMeal = applySwap(workingMeal, "calories");
                case 2 -> workingMeal = applySwap(workingMeal, "protein");
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

    private static List<MealInfo> applySwap(List<MealInfo> meal, String goal) {
        List<MealInfo> updated = new ArrayList<>();

        for (MealInfo item : meal) {
            switch (goal) {
                case "calories" -> {
                    if (item.name.equalsIgnoreCase("Bacon")) {
                        updated.add(new MealInfo("Turkey Bacon", item.grams, 180, 19.0));
                    } else if (item.name.equalsIgnoreCase("Cheddar Cheese")) {
                        updated.add(new MealInfo("Low-Fat Cheese", item.grams, 80, 7.0));
                    } else {
                        updated.add(item);
                    }
                }
                case "protein" -> {
                    if (item.name.equalsIgnoreCase("Apple Juice")) {
                        updated.add(new MealInfo("Soy Milk", item.grams, 100, 7.0));
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
