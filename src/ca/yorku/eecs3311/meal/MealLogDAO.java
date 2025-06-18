package ca.yorku.eecs3311.meal;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MealLogDAO {
    private static final String URL      = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER     = "root";
    private static final String PASSWORD = "Tamjid01711!";

    /**
     * Saves a new MealLog. Returns true on success.
     */
    public boolean save(MealLog m) {
        String sql = "INSERT INTO MealLog "
                + "(profile_name, meal_type, timestamp, food_item, quantity) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getProfileName());
            ps.setString(2, m.getMealType().name());
            ps.setTimestamp(3, Timestamp.valueOf(m.getTimestamp()));
            ps.setString(4, m.getFoodItem());
            ps.setDouble(5, m.getQuantity());

            int affected = ps.executeUpdate();
            if (affected==1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) m = new MealLog(
                            keys.getInt(1),
                            m.getProfileName(),
                            m.getMealType(),
                            m.getTimestamp(),
                            m.getFoodItem(),
                            m.getQuantity()
                    );
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Finds all meals logged by a given user.
     */
    public List<MealLog> findByProfile(String profileName) {
        String sql = "SELECT id, profile_name, meal_type, timestamp, food_item, quantity "
                + "FROM MealLog WHERE profile_name = ?";
        List<MealLog> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profileName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MealLog(
                            rs.getInt("id"),
                            rs.getString("profile_name"),
                            MealType.valueOf(rs.getString("meal_type")),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getString("food_item"),
                            rs.getDouble("quantity")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
