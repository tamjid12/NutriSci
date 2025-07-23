package ca.yorku.eecs3311.nutrient;

import ca.yorku.eecs3311.meal.MealItem;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class NutrientCalculator {
    private static final String URL = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER = "root";
    private static final String PASSWORD = "Tamjid01711!";

    public Map<String, Double> calcForEntry(int entryId) throws SQLException {
        Map<String, ca.yorku.eecs3311.nutrient.NutrientInfo> withUnits = calcForEntryWithUnits(entryId);
        Map<String, Double> plain = new HashMap<>();
        withUnits.forEach((k, v) -> plain.put(k, v.getAmount()));
        return plain;
    }

    public Map<String, Double> calcForItems(List<MealItem> items) throws SQLException {
        Map<String, ca.yorku.eecs3311.nutrient.NutrientInfo> withUnits = calcForItemsWithUnits(items);
        Map<String, Double> plain = new HashMap<>();
        withUnits.forEach((k, v) -> plain.put(k, v.getAmount()));
        return plain;
    }

    public Map<String, ca.yorku.eecs3311.nutrient.NutrientInfo> calcForEntryWithUnits(int entryId) throws SQLException {
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
                Map<String, ca.yorku.eecs3311.nutrient.NutrientInfo> result = new HashMap<>();
                while (rs.next()) {
                    String sym = rs.getString("sym");
                    String unit = rs.getString("unit");
                    double total = rs.getDouble("total");
                    result.put(sym, new ca.yorku.eecs3311.nutrient.NutrientInfo(sym, total, unit));
                }
                return result;
            }
        }
    }

    public Map<String, ca.yorku.eecs3311.nutrient.NutrientInfo> calcForItemsWithUnits(List<MealItem> items) throws SQLException {
        String sql = """
          SELECT nn.NutrientSymbol AS sym,
                 nn.NutrientUnit   AS unit,
                 na.NutrientValue  AS per100g
            FROM food_name      fn
            JOIN nutrient_amount na ON fn.FoodID      = na.FoodID
            JOIN nutrient_name   nn ON na.NutrientID = nn.NutrientID
           WHERE fn.FoodDescription = ?
        """;

        Map<String, ca.yorku.eecs3311.nutrient.NutrientInfo> map = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (MealItem it : items) {
                ps.setString(1, it.getFoodName());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String sym = rs.getString("sym");
                        String unit = rs.getString("unit");
                        double per100g = rs.getDouble("per100g");
                        double add = it.getQuantity() * per100g / 100.0;

                        map.merge(sym,
                                new ca.yorku.eecs3311.nutrient.NutrientInfo(sym, add, unit),
                                (prev, nxt) -> new ca.yorku.eecs3311.nutrient.NutrientInfo(
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

    public Map<String, Double> calcAverageNutrients(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
        SELECT nn.NutrientSymbol AS sym,
               nn.NutrientUnit AS unit,
               SUM(mi.quantity * na.NutrientValue / 100.0) AS total,
               COUNT(DISTINCT DATE(m.entry_time)) as days
        FROM MealEntry m
        JOIN MealItem mi ON m.entry_id = mi.entry_id
        JOIN food_name fn ON mi.food_name = fn.FoodDescription
        JOIN nutrient_amount na ON fn.FoodID = na.FoodID
        JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID
        WHERE DATE(m.entry_time) BETWEEN ? AND ?
        GROUP BY nn.NutrientSymbol, nn.NutrientUnit
    """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Double> result = new HashMap<>();
                while (rs.next()) {
                    String sym = rs.getString("sym");
                    double total = rs.getDouble("total");
                    int days = rs.getInt("days");
                    result.put(sym, total / days);
                }
                return result;
            }
        }
    }
}
