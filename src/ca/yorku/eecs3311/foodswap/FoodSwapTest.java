package ca.yorku.eecs3311.foodswap;

import java.util.*;

public class FoodSwapTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, Double> originalMeal = new LinkedHashMap<>();
        originalMeal.put("White Bread", 100.0);
        originalMeal.put("Bacon", 50.0);

        System.out.println("Original Meal:");
        originalMeal.forEach((k, v) -> System.out.println("- " + k + " (g): " + v));

        System.out.println("\nChoose a goal: [1] Reduce Calories, [2] Increase Protein");
        String choice = scanner.nextLine();

        Map<String, Double> swappedMeal = new LinkedHashMap<>(originalMeal);

        switch (choice) {
            case "1":
                swappedMeal.put("White Bread", 80.0); // reduced
                swappedMeal.put("Bacon", 0.0); // removed
                swappedMeal.put("Avocado", 30.0); // replacement
                break;
            case "2":
                swappedMeal.put("Chicken", 100.0); // add high-protein item
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("\nSuggested Swaps:");
        swappedMeal.forEach((k, v) -> System.out.println("- " + k + " (g): " + v));
    }
}
