package ca.yorku.eecs3311.meal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for handling database operations
 * related to meal entries and their associated food items.
 *
 * Provides methods to Save a MealEntry and its items in a transaction,
 * Load meal entries for a given profile/date,
 * Load all meal entries for a profile,
 * and Delete a meal entry by ID.
 */
public class MealEntryDAO {
    private static final String URL      = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER     = "root";
    private static final String PASSWORD = "Ravenclaw16.";

    /** Inserts a MealEntry and its MealItems in one transaction */
    public boolean save(MealEntry e) {
        String insertEntry = """
            INSERT INTO MealEntry
              (profile_name, meal_type, meal_date, meal_time)
            VALUES (?,?,?,?)
        """;
        String insertItem = """
            INSERT INTO MealItem
              (id, food_name, quantity)
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
            WHERE id = ?
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

    /** Loads all meal entries (with items) for a profile (all dates) */
    public List<MealEntry> findByProfile(String profile) {
        String sqlEntry = """
            SELECT id, meal_type, meal_date, meal_time
            FROM MealEntry
            WHERE profile_name = ?
            ORDER BY meal_date DESC, meal_time DESC
        """;
        String sqlItems = """
            SELECT id, food_name, quantity
            FROM MealItem
            WHERE id = ?
        """;
        List<MealEntry> entries = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD);
             PreparedStatement pe = conn.prepareStatement(sqlEntry)) {

            pe.setString(1, profile);
            try (ResultSet rs = pe.executeQuery()) {
                while (rs.next()) {
                    int entryId = rs.getInt("id");
                    MealType type = MealType.valueOf(rs.getString("meal_type"));
                    LocalDate date = rs.getDate("meal_date").toLocalDate();
                    LocalTime time = rs.getTime("meal_time").toLocalTime();
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
    
    public boolean updateMealItem(int itemId, String newFoodName, double quantity) {
        String sql = "UPDATE MealItem SET food_name = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newFoodName);
            ps.setDouble(2, quantity);
            ps.setInt(3, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /** Deletes a meal entry (and all its items) by id */
    public boolean deleteMealEntry(int id) {
        String sql1 = "DELETE FROM MealItem WHERE id = ?";
        String sql2 = "DELETE FROM MealEntry WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setInt(1, id);
                int rows = ps2.executeUpdate();
                conn.commit();
                return rows > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
