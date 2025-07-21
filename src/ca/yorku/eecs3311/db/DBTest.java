package ca.yorku.eecs3311.db;

import java.sql.*;

/**
 * A simple test utility to check if a connection can be successfully made to the `nutriscidb` MySQL database.
 *  This class connects to the database using JDBC, prints a success message,
 * retrieves and prints the MySQL version, and then closes the connection.
 * The main method that initiates a database connection and prints basic connection metadata.
 */
public class DBTest {

    public static void main(String[] args) {
        try {
            // Establish a connection to the local MySQL database
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/nutriscidb", "root", "pulkit@123!");

            System.out.println("Connected to nutriscidb!");

            // Create and execute a simple query to fetch MySQL version
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION();");

            while (rs.next()) {
                System.out.println("MySQL Version: " + rs.getString(1));
            }

            // Close the connection
            conn.close();

        } catch (Exception e) {
            // Print any exception stack trace for debugging
            e.printStackTrace();
        }
    }
}
