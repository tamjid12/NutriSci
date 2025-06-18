package ca.yorku.eecs3311.ui;

import javax.swing.*;
import java.awt.*;

public class MainApp implements Navigator {
    private JFrame     frame;
    private JPanel     cards;
    private CardLayout cl;
    private String     currentProfile;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().initUI());
    }

    private void initUI() {
        // Optional: switch to Nimbus look & feel
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

        // Initial screens
        cards.add(new MainMenuPanel(this),         "MENU");
        cards.add(new CreateProfilePanel(this),    "CREATE");
        cards.add(new ProfileSelectionPanel(this), "SELECT");
        // Note: MealEntryPanel and JournalPanel are added dynamically

        frame.setContentPane(cards);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
        frame.setMinimumSize(new Dimension(800, 600));
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
        // Recreate the selection panel to refresh the list
        cards.add(new ProfileSelectionPanel(this), "SELECT");
        cl.show(cards, "SELECT");
    }

    @Override
    public void showMealLog(String profileName) {
        // Remember which profile is active
        this.currentProfile = profileName;
        // Add the meal entry screen for this profile
        cards.add(new MealEntryPanel(this, profileName), "MEAL");
        cl.show(cards, "MEAL");
    }

    @Override
    public void showJournal() {
        // Show the journal for the current profile
        cards.add(new JournalPanel(this, currentProfile), "JOURNAL");
        cl.show(cards, "JOURNAL");
    }
}

