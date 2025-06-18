package ca.yorku.eecs3311.db;
/**
 This is for testing connection to DB
 */
import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/nutriscidb", "root", "Tamjid01711!");

            System.out.println("Connected to nutriscidb!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION();");
            while (rs.next()) {
                System.out.println("MySQL Version: " + rs.getString(1));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
