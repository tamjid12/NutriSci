package ca.yorku.eecs3311.profile;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading UserProfile objects from the Profile table.
 */
public class ProfileDAO {
    private static final String URL      = "jdbc:mysql://localhost:3306/nutriscidb";
    private static final String USER     = "root";
    private static final String PASSWORD = "Tamjid01711!";

    /** Inserts a new profile row. */
    public boolean save(UserProfile p) {
        String sql = "INSERT INTO Profile "
                + "(name, sex, dob, height, weight, units) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getSex());
            ps.setDate  (3, Date.valueOf(p.getDob()));
            ps.setDouble(4, p.getHeight());
            ps.setDouble(5, p.getWeight());
            ps.setString(6, p.getUnits().name());

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Retrieves all profiles from the table. */
    public List<UserProfile> findAll() {
        String sql = "SELECT name, sex, dob, height, weight, units FROM Profile";
        List<UserProfile> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String name       = rs.getString("name");
                String sex        = rs.getString("sex");
                LocalDate dob     = rs.getDate("dob").toLocalDate();
                double height     = rs.getDouble("height");
                double weight     = rs.getDouble("weight");
                UnitSystem units  = UnitSystem.valueOf(rs.getString("units"));

                list.add(new UserProfile(name, sex, dob, height, weight, units));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /** Quick standalone test – run this main to verify your DAO works. */
    public static void main(String[] args) {
        ProfileDAO dao = new ProfileDAO();
        UserProfile test = UserProfileFactory.createUserProfile(
                "TestUser", "Male",
                LocalDate.of(2000,1,1),
                1.75, 70.0,
                UnitSystem.METRIC
        );
        System.out.println("Saved? " + dao.save(test));
        dao.findAll().forEach(System.out::println);
    }
}
