// src/ca/yorku/eecs3311/meal/MealEntryDAO.java
package ca.yorku.eecs3311.meal;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MealEntryDAO {
    private static final String URL      = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER     = "root";
    private static final String PASSWORD = "Tamjid01711!";

    /** Inserts a MealEntry and its MealItems in one transaction */
    public boolean save(MealEntry e) {
        String insertEntry = """
            INSERT INTO MealEntry
              (profile_name, meal_type, meal_date, meal_time)
            VALUES (?,?,?,?)
        """;
        String insertItem = """
            INSERT INTO MealItem
              (entry_id, food_name, quantity)
            VALUES (?,?,?)
        """;

        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD)) {
            conn.setAutoCommit(false);

            // 1) save entry
            try (PreparedStatement pe = conn.prepareStatement(
                    insertEntry, Statement.RETURN_GENERATED_KEYS)) {
                pe.setString(1, e.getProfileName());
                pe.setString(2, e.getMealType().name());
                pe.setDate  (3, Date.valueOf(e.getDate()));
                pe.setTime  (4, Time.valueOf(e.getTime()));
                pe.executeUpdate();

                try (ResultSet keys = pe.getGeneratedKeys()) {
                    keys.next();
                    int entryId = keys.getInt(1);

                    // 2) save items
                    try (PreparedStatement pi = conn.prepareStatement(insertItem)) {
                        for (MealItem item : e.getItems()) {
                            pi.setInt   (1, entryId);
                            pi.setString(2, item.getFoodName());
                            pi.setDouble(3, item.getQuantity());
                            pi.addBatch();
                        }
                        pi.executeBatch();
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Loads all entries (and their items) for a profile+date */
    public List<MealEntry> findByProfileAndDate(String profile, LocalDate date) {
        String sqlEntry = """
            SELECT id, meal_type, meal_time
            FROM MealEntry
            WHERE profile_name = ? AND meal_date = ?
        """;
        String sqlItems = """
            SELECT id, food_name, quantity
            FROM MealItem
            WHERE entry_id = ?
        """;

        List<MealEntry> entries = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD);
             PreparedStatement pe = conn.prepareStatement(sqlEntry)) {

            pe.setString(1, profile);
            pe.setDate  (2, Date.valueOf(date));
            try (ResultSet rs = pe.executeQuery()) {
                while (rs.next()) {
                    int    entryId = rs.getInt("id");
                    MealType type   = MealType.valueOf(rs.getString("meal_type"));
                    LocalTime time  = rs.getTime("meal_time").toLocalTime();
                    MealEntry e = new MealEntry(entryId, profile, type, date, time, new ArrayList<>());

                    // fetch items
                    try (PreparedStatement pi = conn.prepareStatement(sqlItems)) {
                        pi.setInt(1, entryId);
                        try (ResultSet rs2 = pi.executeQuery()) {
                            while (rs2.next()) {
                                e.getItems().add(new MealItem(
                                        rs2.getInt("id"),
                                        entryId,
                                        rs2.getString("food_name"),
                                        rs2.getDouble("quantity")
                                ));
                            }
                        }
                    }
                    entries.add(e);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return entries;
    }
}
