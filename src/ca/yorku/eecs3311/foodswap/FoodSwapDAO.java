package ca.yorku.eecs3311.foodswap;

import java.sql.*;
import java.util.*;

public class FoodSwapDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Tamjid01711!"; // Replace securely in production

    /**
     * Suggests food swaps for a given food name and nutritional goal,
     * ensuring swaps are from the same food group.
     */
    public List<String> suggestSwap(String foodName, String goal) {
        List<String> swaps = new ArrayList<>();

        String nutrientSymbol = getNutrientSymbol(goal);
        String comparison = getComparisonOperator(goal);
        String sortOrder = getSortOrder(goal);

        if (nutrientSymbol == null || comparison == null || sortOrder == null) {
            return swaps; // Invalid goal
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // Step 1: Find target food’s group and nutrient value
            String infoQuery =
                    "SELECT fn.FoodID, fn.FoodGroupID, na.NutrientValue " +
                            "FROM food_name fn " +
                            "JOIN nutrient_amount na ON fn.FoodID = na.FoodID " +
                            "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
                            "WHERE fn.FoodDescription LIKE ? " +
                            "AND nn.NutrientSymbol = ? " +
                            "LIMIT 1";

            int groupId = -1;
            double value = -1;

            try (PreparedStatement infoStmt = conn.prepareStatement(infoQuery)) {
                infoStmt.setString(1, "%" + foodName + "%");
                infoStmt.setString(2, nutrientSymbol);
                ResultSet rs = infoStmt.executeQuery();

                if (rs.next()) {
                    groupId = rs.getInt("FoodGroupID");
                    value = rs.getDouble("NutrientValue");
                } else {
                    return swaps; // No match found
                }
            }

            // Step 2: Find better swaps in same food group
            String swapQuery =
                    "SELECT fn.FoodDescription " +
                            "FROM food_name fn " +
                            "JOIN nutrient_amount na ON fn.FoodID = na.FoodID " +
                            "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
                            "WHERE nn.NutrientSymbol = ? " +
                            "AND fn.FoodGroupID = ? " +
                            "AND na.NutrientValue " + comparison + " ? " +
                            "ORDER BY na.NutrientValue " + sortOrder + " " +
                            "LIMIT 3";

            try (PreparedStatement swapStmt = conn.prepareStatement(swapQuery)) {
                swapStmt.setString(1, nutrientSymbol);
                swapStmt.setInt(2, groupId);
                swapStmt.setDouble(3, value);
                ResultSet rs = swapStmt.executeQuery();
                while (rs.next()) {
                    swaps.add(rs.getString("FoodDescription"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return swaps;
    }

    /** Maps goal to nutrient symbol in DB */
    private String getNutrientSymbol(String goal) {
        return switch (goal) {
            case "Increase Protein" -> "PROT";
            case "Reduce Calories" -> "KCAL";
            case "Increase Fiber"  -> "FIB";
            case "Reduce Sugar"    -> "SUGAR";
            default -> null;
        };
    }

    /** Comparison operator: greater or less */
    private String getComparisonOperator(String goal) {
        return switch (goal) {
            case "Increase Protein", "Increase Fiber" -> ">";
            case "Reduce Calories", "Reduce Sugar"    -> "<";
            default -> null;
        };
    }

    /** Sorting order to get best match */
    private String getSortOrder(String goal) {
        return switch (goal) {
            case "Increase Protein", "Increase Fiber" -> "DESC";
            case "Reduce Calories", "Reduce Sugar"    -> "ASC";
            default -> null;
        };
    }
}