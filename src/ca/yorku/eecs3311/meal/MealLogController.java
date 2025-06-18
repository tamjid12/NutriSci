// src/ca/yorku/eecs3311/meal/MealLogController.java
package ca.yorku.eecs3311.meal;

import java.time.LocalDate;
import java.util.List;

public class MealLogController {
    private MealEntryDAO dao = new MealEntryDAO();

    /** Save a full MealEntry (with items) */
    public boolean saveMeal(MealEntry entry) {
        return dao.save(entry);
    }

    /** Get all MealEntries for a user on a given date */
    public List<MealEntry> getMealsForUserOnDate(String profile, LocalDate date) {
        return dao.findByProfileAndDate(profile, date);
    }
}
