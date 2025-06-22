package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.profile.UserProfile;

public interface Navigator {
    void showMainMenu();
    void showCreateProfile();                  // create mode
    void showCreateProfile(UserProfile toEdit); // edit mode
    void showSelectProfile();
    void showMealLog(String profileName);
    void showJournal();
}
