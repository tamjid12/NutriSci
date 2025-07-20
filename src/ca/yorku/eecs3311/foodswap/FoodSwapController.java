// FoodSwapTest.java
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
    private static final String URL = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER = "root";
    private static final String PASSWORD = "Tamjid01711!";

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

    public SwapInfo getNutrientSummary(String foodName, double quantity) {
        try {
            String sql = "SELECT nn.NutrientSymbol, na.NutrientValue FROM food_name fn " +
                         "JOIN nutrient_amount na ON fn.FoodID = na.FoodID " +
                         "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
                         "WHERE fn.FoodDescription = ?";

            double kcals = 0.0, protein = 0.0;

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, foodName);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String symbol = rs.getString("NutrientSymbol");
                        double valPer100g = rs.getDouble("NutrientValue");
                        double adjusted = valPer100g * quantity / 100.0;

                        if (symbol.equals("KCAL")) kcals = adjusted;
                        if (symbol.equals("PROT")) protein = adjusted;
                    }
                }
            }

            return new SwapInfo(quantity, kcals, protein);
        } catch (Exception e) {
            e.printStackTrace();
            return new SwapInfo(quantity, 0, 0);
        }
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

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT fn.FoodDescription, na.NutrientValue " +
                "FROM food_name fn " +
                "JOIN nutrient_amount na ON fn.FoodID = na.FoodID " +
                "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
                "WHERE nn.NutrientSymbol = ? " +
                "ORDER BY na.NutrientValue " + (findHigher ? "DESC" : "ASC") + " LIMIT 5")) {

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
