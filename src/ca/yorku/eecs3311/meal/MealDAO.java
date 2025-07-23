package ca.yorku.eecs3311.meal;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MealDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER = "root";
    private static final String PASSWORD = "Tamjid01711!";

    public List<MealEntry> getMealsForUserBetweenDates(String profileName, LocalDate start, LocalDate end) throws SQLException {
        List<MealEntry> entries = new ArrayList<>();

        String sql = """
            SELECT * FROM MealEntry
            WHERE profile_name = ?
            AND meal_date BETWEEN ? AND ?
            ORDER BY meal_date, meal_time
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profileName);
            ps.setDate(2, java.sql.Date.valueOf(start));
            ps.setDate(3, java.sql.Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("profile_name");
                    MealType type = MealType.valueOf(rs.getString("meal_type").toUpperCase());
                    LocalDate date = rs.getDate("meal_date").toLocalDate();
                    LocalTime time = rs.getTime("meal_time").toLocalTime();

                    MealEntry entry = new MealEntry(id, name, type, date, time, new ArrayList<>());
                    entries.add(entry);
                }
            }
        }

        return entries;
    }
}

