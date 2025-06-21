package ca.yorku.eecs3311.foodswap;

import java.util.*;

public class FoodSwap {
    private Map<String, Double> originalMeal;
    private Map<String, Double> swappedMeal;

    public FoodSwap(Map<String, Double> originalMeal) {
        this.originalMeal = originalMeal;
        this.swappedMeal = new HashMap<>();
    }

    public void suggestSwap(String goal) {
        // Simplified swap logic: replace items purely by name without nutrient info
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
