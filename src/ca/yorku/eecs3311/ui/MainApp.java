package ca.yorku.eecs3311.ui;

import javax.swing.*;
import java.awt.*;

public class MainApp implements Navigator {
    private JFrame     frame;
    private JPanel     cards;
    private CardLayout cl;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().initUI());
    }

    private void initUI() {
        // Optional: Nimbus L&F
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

        // Register panels
        cards.add(new MainMenuPanel(this),        "MENU");
        cards.add(new CreateProfilePanel(this),   "CREATE");
        cards.add(new ProfileSelectionPanel(this),"SELECT");
        // MealLogPanel will be added dynamically

        frame.setContentPane(cards);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showMainMenu();
    }

    @Override public void showMainMenu() {
        cl.show(cards, "MENU");
    }
    @Override public void showCreateProfile() {
        cl.show(cards, "CREATE");
    }
    @Override public void showSelectProfile() {
        // re-create to refresh
        cards.add(new ProfileSelectionPanel(this), "SELECT");
        cl.show(cards, "SELECT");
    }
    @Override public void showMealLog(String profileName) {
        cards.add(new MealLogPanel(this, profileName), "MEAL");
        cl.show(cards, "MEAL");
    }
}
