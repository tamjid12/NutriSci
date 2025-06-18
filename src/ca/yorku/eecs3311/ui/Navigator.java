package ca.yorku.eecs3311.ui;

/**
 * Navigation contract between panels and the main frame.
 */
public interface Navigator {
    void showMainMenu();
    void showCreateProfile();
    void showSelectProfile();
    void showMealLog(String profileName);
}
