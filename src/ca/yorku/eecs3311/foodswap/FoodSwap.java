package ca.yorku.eecs3311.foodswap;

import java.util.*;

/**
 * The class provides a mock implementation for suggesting healthier or alternative food swaps
 * based on a user-defined dietary goal.
 * This class operates on a simple {@code Map<String, Double>} that represents the original meal,
 * where the key is the food name and the value is its quantity in grams. The method {@code suggestSwap}
 * currently applies a hardcoded swap strategy, such as replacing "Toast" with "Whole Grain Toast"
 * when the goal is "reduce calories".
 * This is a placeholder version and does not perform real nutrient-based analysis.
 */
public class FoodSwap {

    //Stores the original meal with food names and quantities in grams. */
    private Map<String, Double> originalMeal;

    //Stores the meal after applying a swap suggestion. */
    private Map<String, Double> swappedMeal;


    //Constructs a FoodSwap instance using the original meal data.
    public FoodSwap(Map<String, Double> originalMeal) {
        this.originalMeal = originalMeal;
        this.swappedMeal = new HashMap<>();
    }

    //Suggests food swaps based on the provided dietary goal.
    public void suggestSwap(String goal) {
        // Simplified swap logic: replace specific food items based on goal
        for (String food : originalMeal.keySet()) {
            double quantity = originalMeal.get(food);
            if (goal.equalsIgnoreCase("reduce calories")) {
                if (food.equalsIgnoreCase("Toast")) {
                    swappedMeal.put("Whole Grain Toast", quantity);
                } else {
                    swappedMeal.put(food, quantity);
                }
            } else {
                swappedMeal.put(food, quantity);
            }
        }

        System.out.println("[Mock] Swap suggested based on goal: " + goal);
        printSwap();
    }


    //Prints both the original meal and the swapped meal for debugging or demonstration purposes.

    private void printSwap() {
        System.out.println("Original Meal:");
        for (Map.Entry<String, Double> entry : originalMeal.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue() + "g");
        }

        System.out.println("Swapped Meal:");
        for (Map.Entry<String, Double> entry : swappedMeal.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue() + "g");
        }
    }
}
