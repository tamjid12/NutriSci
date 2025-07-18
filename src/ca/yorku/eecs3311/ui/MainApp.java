package ca.yorku.eecs3311.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ca.yorku.eecs3311.meal.MealItem;
import ca.yorku.eecs3311.profile.UserProfile;

/**
 * The main application class responsible for initializing the GUI and handling screen navigation.
 * It uses a CardLayout to manage multiple panels and implements the Navigator interface
 * to allow screen transitions from anywhere in the application.
 */

public class MainApp implements Navigator {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cl;
    private String currentProfile;

    // track panels by name so we can replace them
    private final Map<String, JPanel> cardMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().initUI());
    }
    //Initializes the user interface: frame, layout, and initial panels.
    private void initUI() {
        // Nimbus L&F
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        frame = new JFrame("NutriSci Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cl    = new CardLayout();
        cards = new JPanel(cl);

        // Static cards
        MainMenuPanel menu = new MainMenuPanel(this);
        cards.add(menu, "MENU");        cardMap.put("MENU", menu);

        // we’ll add empty CREATE here so key always exists
        CreateProfilePanel create0 = new CreateProfilePanel(this);
        cards.add(create0, "CREATE");  cardMap.put("CREATE", create0);

        ProfileSelectionPanel select0 = new ProfileSelectionPanel(this);
        cards.add(select0, "SELECT");  cardMap.put("SELECT", select0);

        frame.setContentPane(cards);
        frame.pack();
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showMainMenu();
    }
    //Main Menu panel
    @Override
    public void showMainMenu() {
        cl.show(cards, "MENU");
    }
    // Opens a fresh CreateProfile panel for creating a new user
    @Override
    public void showCreateProfile() {
        // Remove old CREATE panel
        JPanel old = cardMap.remove("CREATE");
        if (old != null) cards.remove(old);

        // Add a fresh blank form
        CreateProfilePanel cp = new CreateProfilePanel(this);
        cards.add(cp, "CREATE");
        cardMap.put("CREATE", cp);
        cl.show(cards, "CREATE");
    }

    @Override
    public void showCreateProfile(UserProfile toEdit) {
        // Remove old CREATE panel
        JPanel old = cardMap.remove("CREATE");
        if (old != null) cards.remove(old);

        // Add fresh SELECT panel
        CreateProfilePanel cp = new CreateProfilePanel(this, toEdit);
        cards.add(cp, "CREATE");
        cardMap.put("CREATE", cp);
        cl.show(cards, "CREATE");
    }

   // @Override

    public void showSelectProfile() {
        // Remove old SELECT panel
        JPanel old = cardMap.remove("SELECT");
        if (old != null) cards.remove(old);

        // Add fresh SELECT panel
        ProfileSelectionPanel sel = new ProfileSelectionPanel(this);
        cards.add(sel, "SELECT");
        cardMap.put("SELECT", sel);
        cl.show(cards, "SELECT");
    }

    // Opens the MealEntry panel for the selected profile.
    @Override
    public void showMealLog(String profileName) {
        this.currentProfile = profileName;
        JPanel old = cardMap.remove("MEAL");
        if (old != null) cards.remove(old);

        MealEntryPanel meal = new MealEntryPanel(this, profileName);
        cards.add(meal, "MEAL");
        cardMap.put("MEAL", meal);
        cl.show(cards, "MEAL");
    }

    // Opens the Journal panel for the current profile.
    @Override
    public void showJournal() {
        JPanel old = cardMap.remove("JOURNAL");
        if (old != null) cards.remove(old);

        JournalPanel journal = new JournalPanel(this, currentProfile);
        cards.add(journal, "JOURNAL");
        cardMap.put("JOURNAL", journal);
        cl.show(cards, "JOURNAL");
    }
    public void showFoodSwapPanel(List<MealItem> currentMeal) {
        JPanel old = cardMap.remove("FOODSWAP");
        if (old != null) cards.remove(old);

        FoodSwapPanel fs = new FoodSwapPanel(currentMeal, this);
        cards.add(fs, "FOODSWAP");
        cardMap.put("FOODSWAP", fs);
        cl.show(cards, "FOODSWAP");
    }

}
