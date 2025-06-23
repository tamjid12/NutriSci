package ca.yorku.eecs3311.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *  The main menu panel shown when the application starts.
 *  Provides access to profile creation, profile selection, and app exit.
 *  Uses a clean layout with modern styling for buttons and titles.
 */
public class MainMenuPanel extends JPanel {
    private final Navigator nav;
    // Constructs the main menu panel.
    public MainMenuPanel(Navigator nav) {
        this.nav = nav;
        initUI();
    }
    /**
     * Initializes the UI components for the main menu, including the title,
     * subtitle, action buttons, and layout design.
     */
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title & Subtitle
        JLabel title = new JLabel("NutriSci Tracker", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setForeground(new Color(30, 30, 30));

        JLabel subtitle = new JLabel(
                "Welcome to Nutrient Tracker Application",
                SwingConstants.CENTER
        );
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 16f));
        subtitle.setForeground(new Color(80, 80, 80));

        JPanel header = new JPanel(new GridLayout(2,1,0,5));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // Create & Select buttons
        JButton btnCreate = makeMainButton("Create Profile");
        JButton btnSelect = makeMainButton("Select Profile");

        btnCreate.addActionListener(e -> nav.showCreateProfile());
        btnSelect.addActionListener(e -> nav.showSelectProfile());

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        center.setOpaque(false);
        center.add(btnCreate);
        center.add(btnSelect);

        add(center, BorderLayout.CENTER);

        // Exit button at bottom
        JButton exitBtn = new JButton("Exit App");
        exitBtn.setFocusPainted(false);
        exitBtn.setBackground(new Color(200, 50, 50));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(exitBtn.getFont().deriveFont(Font.BOLD, 12f));
        exitBtn.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        exitBtn.addActionListener(e -> System.exit(0));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(exitBtn, BorderLayout.EAST);

        add(south, BorderLayout.SOUTH);
    }
    //Creates a standard main menu button
    private JButton makeMainButton(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(160, 80));
        b.setFocusPainted(false);
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }
}
