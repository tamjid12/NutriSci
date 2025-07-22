package ca.yorku.eecs3311.ui;

import java.util.List;

import ca.yorku.eecs3311.meal.MealItem;
import ca.yorku.eecs3311.profile.UserProfile;
/**
 * The Navigator interface defines the main navigation operations used by the UI components
 * to switch between different views in the application.
 *
 * Implemented by MainApp, this allows any panel
 * to trigger view transitions without needing to know the details of how they are implemented.
 */
public interface Navigator {
    void showMainMenu();
    void showCreateProfile();                  // create mode
    void showCreateProfile(UserProfile toEdit); // edit mode
    void showSelectProfile();
    void showMealLog(String profileName);
    void showJournal();
    void showFoodSwapPanel(List<MealItem> currentMeal);
    void showCalorieIntakePanel(String profileName);


}
