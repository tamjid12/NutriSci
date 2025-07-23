package ca.yorku.eecs3311.meal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MealDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER = "root";
    private static final String PASSWORD = "Ravenclaw16.";

    public List<MealEntry> getMealsForUserBetweenDates(String profileName, LocalDate start, LocalDate end) throws SQLException {
        List<MealEntry> meals = new ArrayList<>();

        String entrySQL = """
            SELECT id, meal_type, meal_date, meal_time
            FROM MealEntry
            WHERE profile_name = ? AND meal_date BETWEEN ? AND ?
            ORDER BY meal_date, meal_time
        """;

        String itemSQL = """
            SELECT id, food_name, quantity
            FROM MealItem
            WHERE id = ?
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pe = conn.prepareStatement(entrySQL)) {

            pe.setString(1, profileName);
            pe.setDate(2, Date.valueOf(start));
            pe.setDate(3, Date.valueOf(end));

            try (ResultSet rs = pe.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    MealType type = MealType.valueOf(rs.getString("meal_type"));
                    LocalDate date = rs.getDate("meal_date").toLocalDate();
                    LocalTime time = rs.getTime("meal_time").toLocalTime();

                    List<MealItem> items = new ArrayList<>();

                    try (PreparedStatement pi = conn.prepareStatement(itemSQL)) {
                        pi.setInt(1, id);
                        try (ResultSet rs2 = pi.executeQuery()) {
                            while (rs2.next()) {
                                MealItem item = new MealItem(
                                        rs2.getInt("id"),
                                        id,
                                        rs2.getString("food_name"),
                                        rs2.getDouble("quantity")
                                );
                                items.add(item);
                            }
                        }
                    }

                    meals.add(new MealEntry(id, profileName, type, date, time, items));
                }
            }
        }

        return meals;
    }
}
