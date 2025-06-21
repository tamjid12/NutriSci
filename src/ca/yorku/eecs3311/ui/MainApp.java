package ca.yorku.eecs3311.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainApp implements Navigator {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cl;
    private String currentProfile;

    // Keep track of panels by name so we can remove old ones
    private final Map<String, JPanel> cardMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().initUI());
    }

    private void initUI() {
        // Nimbus look & feel
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

        CreateProfilePanel create = new CreateProfilePanel(this);
        cards.add(create, "CREATE");    cardMap.put("CREATE", create);

        ProfileSelectionPanel select = new ProfileSelectionPanel(this);
        cards.add(select, "SELECT");    cardMap.put("SELECT", select);

        frame.setContentPane(cards);
        frame.pack();
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showMainMenu();
    }

    @Override
    public void showMainMenu() {
        cl.show(cards, "MENU");
    }

    @Override
    public void showCreateProfile() {
        cl.show(cards, "CREATE");
    }

    @Override
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

    @Override
    public void showMealLog(String profileName) {
        this.currentProfile = profileName;

        // Remove old MEAL panel
        JPanel old = cardMap.remove("MEAL");
        if (old != null) cards.remove(old);

        // Add fresh MEAL panel
        MealEntryPanel meal = new MealEntryPanel(this, profileName);
        cards.add(meal, "MEAL");
        cardMap.put("MEAL", meal);
        cl.show(cards, "MEAL");
    }

    @Override
    public void showJournal() {
        // Remove old JOURNAL panel
        JPanel old = cardMap.remove("JOURNAL");
        if (old != null) cards.remove(old);

        // Add fresh JOURNAL panel
        JournalPanel journal = new JournalPanel(this, currentProfile);
        cards.add(journal, "JOURNAL");
        cardMap.put("JOURNAL", journal);
        cl.show(cards, "JOURNAL");
    }
}
