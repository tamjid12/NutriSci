package ca.yorku.eecs3311.foodswap;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for demonstrating the functionality of the FoodSwap class.
 * This class creates a sample meal and applies a mock food swap strategy
 * to simulate how food substitutions might occur based on a user-defined goal.
 */
public class FoodSwapTest {

    // Main method to run a simple test of the {@code FoodSwap} class.
    public static void main(String[] args) {
        // Example meal setup
        Map<String, Double> ingredients = new HashMap<>();
        ingredients.put("Egg", 100.0);
        ingredients.put("Toast", 50.0);

        // Create the FoodSwap instance with the original meal
        FoodSwap swap = new FoodSwap(ingredients);

        // Apply the swap suggestion for the goal "reduce calories"
        swap.suggestSwap("reduce calories");
    }
}
