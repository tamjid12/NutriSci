package ca.yorku.eecs3311.foodswap;

import java.util.HashMap;
import java.util.Map;

public class FoodSwapApp {
    public static void main(String[] args) {
        // Example meal
        Map<String, Double> ingredients = new HashMap<>();
        ingredients.put("Egg", 100.0);
        ingredients.put("Toast", 50.0);

        // Create swapper
        FoodSwap swap = new FoodSwap(ingredients);
        swap.suggestSwap("reduce calories");
    }
}
