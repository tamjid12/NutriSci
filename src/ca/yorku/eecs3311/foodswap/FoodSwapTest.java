// FoodSwapTest.java
package ca.yorku.eecs3311.foodswap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.yorku.eecs3311.meal.MealItem;

public class FoodSwapTest {
    public static void main(String[] args) {
        // Sample current meal
        List<MealItem> currentMeal = new ArrayList<>();
        currentMeal.add(new MealItem(0, 0, "White Bread", 100.0));
        currentMeal.add(new MealItem(0, 0, "Bacon", 50.0));

        // Convert List<MealItem> to Map<String, Double>
        Map<String, Double> mealMap = new HashMap<>();
        for (MealItem item : currentMeal) {
            mealMap.put(item.getFoodName(), item.getQuantity());
        }

        // Initialize controller and test swap
        FoodSwapController controller = new FoodSwapController(mealMap);
        Map<String, Double> swapped = controller.suggestSwap("Reduce Calories", currentMeal);

        System.out.println("Original Meal:");
        for (MealItem item : currentMeal) {
            System.out.println("- " + item.getFoodName() + " (g): " + item.getQuantity());
        }

        System.out.println("\nSuggested Swaps:");
        for (Map.Entry<String, Double> entry : swapped.entrySet()) {
            System.out.println("- " + entry.getKey() + " (g): " + entry.getValue());
        }
    }
}