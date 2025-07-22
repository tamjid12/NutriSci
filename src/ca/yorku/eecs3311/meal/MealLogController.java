// src/ca/yorku/eecs3311/meal/MealLogController.java
package ca.yorku.eecs3311.meal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MealLogController {
    private MealEntryDAO dao = new MealEntryDAO();

    /**
     * Save a full MealEntry (with items)
     */
    public boolean saveMeal(MealEntry entry) {
        return dao.save(entry);
    }

    /**
     * Get all MealEntries for a user on a given date
     */
    public List<MealEntry> getMealsForUserOnDate(String profile, LocalDate date) {
        return dao.findByProfileAndDate(profile, date);
    }

    public List<MealEntry> getMealsForUserBetweenDates(String profileName, LocalDate start, LocalDate end) throws SQLException, SQLException {
        MealDAO dao = new MealDAO();
        try {
            return dao.getMealsForUserBetweenDates(profileName, start, end);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}