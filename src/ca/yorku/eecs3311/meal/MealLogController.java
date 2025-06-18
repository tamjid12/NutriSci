package ca.yorku.eecs3311.meal;

import java.util.List;

public class MealLogController {
    private MealLogDAO dao = new MealLogDAO();

    public boolean saveMeal(MealLog m) {
        return dao.save(m);
    }

    public List<MealLog> getMealsForUser(String profileName) {
        return dao.findByProfile(profileName);
    }
}
