package ca.yorku.eecs3311.meal;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MealLogController {
    private final MealEntryDAO dao = new MealEntryDAO();
    private final MealDAO mealDAO = new MealDAO();

    public boolean saveMeal(MealEntry entry) {
        return dao.save(entry);
    }

    public List<MealEntry> getMealsForUserOnDate(String profile, LocalDate date) {
        return dao.findByProfileAndDate(profile, date);
    }

    public List<MealEntry> getMealsForUserBetweenDates(String profileName, LocalDate start, LocalDate end) throws SQLException {
        return mealDAO.getMealsForUserBetweenDates(profileName, start, end); // mealDAO now uses "id" instead of entry_id
    }
}
