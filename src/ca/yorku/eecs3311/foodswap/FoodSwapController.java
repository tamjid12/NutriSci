package ca.yorku.eecs3311.foodswap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.yorku.eecs3311.meal.MealItem;

public class FoodSwapController {

    private final Map<String, Double> originalMeal;

    public FoodSwapController(Map<String, Double> originalMeal) {
        this.originalMeal = originalMeal;
    }

    public Map<String, Double> suggestSwap(String goal, List<MealItem> currentMeal) {
        Map<String, Double> swapResult = new HashMap<>();
        Map<String, Double> originalMeal = new HashMap<>();

        for (MealItem item : currentMeal) {
            originalMeal.put(item.getFoodName(), item.getQuantity());
        }

        // Fetch top 5 swaps based on goal
        Map<String, Double> suggested = findSwap(goal);

        if (suggested.isEmpty()) {
            return originalMeal; // fallback to original if no swap found
        }

        // Replace one or two items max
        int replaced = 0;
        for (String food : originalMeal.keySet()) {
            if (replaced < 2 && suggested.containsKey(food)) {
                swapResult.put(food + " (Swapped)", suggested.get(food));
                replaced++;
            } else {
                swapResult.put(food, originalMeal.get(food));
            }
        }

        return swapResult;
    }

    private Map<String, Double> findSwap(String goal) {
        Map<String, Double> alternatives = new HashMap<>();

        // Map goal to nutrient symbol
        String nutrientSymbol;
        boolean findHigher = false;

        switch (goal.toLowerCase()) {
            case "increase fiber":
                nutrientSymbol = "FIBTG";
                findHigher = true;
                break;
            case "reduce calories":
                nutrientSymbol = "ENERC";
                break;
            case "reduce sugar":
                nutrientSymbol = "SUGAR";
                break;
            case "increase protein":
                nutrientSymbol = "PROCNT";
                findHigher = true;
                break;
            default:
                return alternatives;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/nutriscidb", "root", "Tamjid01711!");
            PreparedStatement stmt = conn.prepareStatement(
                """
                SELECT fn.FoodDescription, na.NutrientValue
                FROM food_name fn
                JOIN nutrient_amount na ON fn.FoodID = na.FoodID
                JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID
                WHERE nn.NutrientSymbol = ?
                ORDER BY na.NutrientValue """ + (findHigher ? " DESC " : " ASC ") + " LIMIT 5 "
            )){

            stmt.setString(1, nutrientSymbol);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String food = rs.getString("FoodDescription");
                double grams = 100.0;
                alternatives.put(food, grams);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alternatives;
    }
}
