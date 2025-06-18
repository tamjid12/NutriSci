package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.meal.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Panel to log a meal for the selected profile.
 */
public class MealLogPanel extends JPanel {
    public MealLogPanel(Navigator nav, String profileName) {
        setBorder(BorderFactory.createTitledBorder("Log Meal for: " + profileName));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        int y=0;
        // Meal type
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Meal Type:"), gbc);
        JComboBox<MealType> typeBox = new JComboBox<>(MealType.values());
        gbc.gridx=1; add(typeBox, gbc);

        // Food item
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("Food Item:"), gbc);
        JTextField foodField = new JTextField(15);
        gbc.gridx=1; add(foodField, gbc);

        // Quantity
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("Quantity:"), gbc);
        JTextField qtyField = new JTextField(5);
        gbc.gridx=1; add(qtyField, gbc);

        // Buttons + status
        JButton save = new JButton("Save Meal");
        JButton back = new JButton("Back");
        JLabel status = new JLabel(" ");
        JPanel btns = new JPanel();
        btns.add(back);
        btns.add(save);

        gbc.gridy=++y; gbc.gridx=0; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        add(btns, gbc);
        gbc.gridy=++y;
        add(status, gbc);

        save.addActionListener(e -> {
            try {
                MealLog m = new MealLog(
                        profileName,
                        (MealType) typeBox.getSelectedItem(),
                        LocalDateTime.now(),
                        foodField.getText().trim(),
                        Double.parseDouble(qtyField.getText().trim())
                );
                boolean ok = new MealLogController().saveMeal(m);
                status.setText(ok ? "Saved!" : "Failed to save.");
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });
        back.addActionListener(e -> nav.showSelectProfile());
    }
}
