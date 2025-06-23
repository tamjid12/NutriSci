package ca.yorku.eecs3311.nutrient;

import ca.yorku.eecs3311.meal.MealItem;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes nutrient totals for meal entries or raw item lists,
 * using the CNF tables: food_name, nutrient_amount, nutrient_name.
 */
public class NutrientCalculator {
    private static final String URL      = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER     = "root";
    private static final String PASSWORD = "Tamjid01711!";

    /**
     * Calculate nutrient totals for a saved meal entry
     */
    public Map<String, NutrientInfo> calcForEntryWithUnits(int entryId) throws SQLException {
        String sql = """
          SELECT nn.NutrientSymbol   AS sym,
                 nn.NutrientUnit     AS unit,
                 SUM(mi.quantity * na.NutrientValue / 100.0) AS total
            FROM MealItem mi
            JOIN food_name      fn ON mi.food_name = fn.FoodDescription
            JOIN nutrient_amount na ON fn.FoodID        = na.FoodID
            JOIN nutrient_name   nn ON na.NutrientID    = nn.NutrientID
           WHERE mi.entry_id = ?
           GROUP BY nn.NutrientSymbol, nn.NutrientUnit
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, NutrientInfo> result = new HashMap<>();
                while (rs.next()) {
                    String sym    = rs.getString("sym");
                    String unit   = rs.getString("unit");
                    double total  = rs.getDouble("total");
                    result.put(sym, new NutrientInfo(sym, total, unit));
                }
                return result;
            }
        }
    }

    /**
     * Calculate nutrient totals from a list of MealItem
     */
    public Map<String, NutrientInfo> calcForItemsWithUnits(List<MealItem> items) throws SQLException {
        String sql = """
          SELECT nn.NutrientSymbol AS sym,
                 nn.NutrientUnit   AS unit,
                 na.NutrientValue  AS per100g
            FROM food_name      fn
            JOIN nutrient_amount na ON fn.FoodID      = na.FoodID
            JOIN nutrient_name   nn ON na.NutrientID = nn.NutrientID
           WHERE fn.FoodDescription = ?
        """;

        Map<String, NutrientInfo> map = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (MealItem it : items) {
                ps.setString(1, it.getFoodName());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String sym     = rs.getString("sym");
                        String unit    = rs.getString("unit");
                        double per100g = rs.getDouble("per100g");
                        double add     = it.getQuantity() * per100g / 100.0;

                        map.merge(sym,
                                new NutrientInfo(sym, add, unit),
                                (prev, nxt) -> new NutrientInfo(
                                        sym,
                                        prev.getAmount() + nxt.getAmount(),
                                        unit
                                ));
                    }
                }
            }
        }
        return map;
    }
}
