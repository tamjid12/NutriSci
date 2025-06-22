package ca.yorku.eecs3311.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The main menu panel shown at startup.
 * Provides "Create Profile", "Select Profile" buttons,
 * plus an "Exit App" button in the bottom‐right.
 */
public class MainMenuPanel extends JPanel {
    private final Navigator nav;

    public MainMenuPanel(Navigator nav) {
        this.nav = nav;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- North: Title + Subtitle ---
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

        // --- Center: Create & Select buttons ---
        JButton btnCreate = makeMainButton("Create Profile");
        JButton btnSelect = makeMainButton("Select Profile");

        btnCreate.addActionListener(e -> nav.showCreateProfile());
        btnSelect.addActionListener(e -> nav.showSelectProfile());

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        center.setOpaque(false);
        center.add(btnCreate);
        center.add(btnSelect);

        add(center, BorderLayout.CENTER);

        // --- South: Exit button at bottom‐right ---
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
