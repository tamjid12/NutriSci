package ca.yorku.eecs3311.foodswap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class FoodSwapDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER = "root";
    private static final String PASSWORD = "Tamjid01711!";

    /**
     * Suggests a food swap based on the nutritional goal.
     *
     * @param originalFood  Name of the current food
     * @param goal          Nutrient goal (e.g., Reduce Calories, Increase Protein)
     * @return A list of candidate replacement food descriptions
     */
    public List<String> suggestSwap(String originalFood, String goal) {
        List<String> results = new ArrayList<>();

        String nutrientSymbol = switch (goal) {
            case "Reduce Calories" -> "KCAL";
            case "Increase Protein" -> "PROT";
            case "Reduce Sugar" -> "SUGAR";
            case "Increase Fiber" -> "FIBTG";
            default -> null;
        };

        if (nutrientSymbol == null) return results;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int foodGroupId = getFoodGroupId(conn, originalFood);
            if (foodGroupId == -1) return results;

            int targetNutrientId = getNutrientId(conn, nutrientSymbol);
            if (targetNutrientId == -1) return results;

            // Find candidate food in the same group, ordered by the nutrient
            String sql = "SELECT fn.FoodDescription, na.NutrientValue " +
                    "FROM food_name fn " +
                    "JOIN nutrient_amount na ON fn.FoodID = na.FoodID " +
                    "WHERE fn.FoodGroupID = ? " +
                    "AND na.NutrientID = ? " +
                    "ORDER BY na.NutrientValue " + (goal.startsWith("Increase") ? "DESC" : "ASC") + " " +
                    "LIMIT 3";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, foodGroupId);
                ps.setInt(2, targetNutrientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString("FoodDescription"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Finds the FoodGroupID of a given food
     */
    private int getFoodGroupId(Connection conn, String foodDescription) throws SQLException {
        String sql = "SELECT FoodGroupID FROM food_name WHERE FoodDescription = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, foodDescription);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("FoodGroupID");
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves the NutrientID for a nutrient symbol like 'KCAL' or 'PROT'
     */
    private int getNutrientId(Connection conn, String nutrientSymbol) throws SQLException {
        String sql = "SELECT NutrientID FROM nutrient_name WHERE NutrientSymbol = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nutrientSymbol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("NutrientID");
                }
            }
        }
        return -1;
    }
}
