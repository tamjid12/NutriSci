package ca.yorku.eecs3311.profile;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for handling CRUD operations related to user profiles.
 * Provides methods to save, retrieve, update, and delete profiles from the database.
 */

public class ProfileDAO {
    private static final String URL  = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER = "root";
    private static final String PASS = "pulkit@123!";

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Insert a new profile. */
    public boolean save(UserProfile p) {
        String sql = """
            INSERT INTO Profile
              (name, sex, dob, height, weight, units)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getSex());
            ps.setDate(3, Date.valueOf(p.getDob()));
            ps.setDouble(4, p.getHeight());
            ps.setDouble(5, p.getWeight());
            ps.setString(6, p.getUnits().name());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Fetch all profiles. */
    public List<UserProfile> findAll() {
        String sql = "SELECT name, sex, dob, height, weight, units FROM Profile";
        List<UserProfile> list = new ArrayList<>();
        try (Connection c = getConn();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name   = rs.getString("name");
                String sex    = rs.getString("sex");
                LocalDate dob = rs.getDate("dob").toLocalDate();
                double height = rs.getDouble("height");
                double weight = rs.getDouble("weight");
                UnitSystem units = UnitSystem.valueOf(rs.getString("units"));
                list.add(new UserProfile(name, sex, dob, height, weight, units));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /** Fetch one profile by name. */
    public UserProfile findByName(String name) {
        String sql = """
            SELECT name, sex, dob, height, weight, units
              FROM Profile
             WHERE name = ?
            """;
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String sex    = rs.getString("sex");
                    LocalDate dob = rs.getDate("dob").toLocalDate();
                    double height = rs.getDouble("height");
                    double weight = rs.getDouble("weight");
                    UnitSystem units = UnitSystem.valueOf(rs.getString("units"));
                    return new UserProfile(name, sex, dob, height, weight, units);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /** Update an existing profile. */
    public boolean update(UserProfile p) {
        String sql = """
            UPDATE Profile
               SET sex    = ?,
                   dob    = ?,
                   height = ?,
                   weight = ?,
                   units  = ?
             WHERE name   = ?
            """;
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getSex());
            ps.setDate(2, Date.valueOf(p.getDob()));
            ps.setDouble(3, p.getHeight());
            ps.setDouble(4, p.getWeight());
            ps.setString(5, p.getUnits().name());
            ps.setString(6, p.getName());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Delete a profile. */
    public boolean delete(String name) {
        String sql = "DELETE FROM Profile WHERE name = ?";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
