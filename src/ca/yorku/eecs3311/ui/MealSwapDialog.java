package ca.yorku.eecs3311.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MealSwapDialog extends JDialog {
    private final List<JTextArea> foodFields = new ArrayList<>();
    private final List<JTextField> qtyFields = new ArrayList<>();
    private boolean confirmed = false;

    public MealSwapDialog(JFrame parent, List<String> foods, List<String> qtys) {
        super(parent, "Update Foods in Meal", true);
        setLayout(new BorderLayout(12, 12));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Table header
        JPanel headPanel = new JPanel(new GridLayout(1, 4, 10, 2));
        JLabel foodLbl = new JLabel("Food", SwingConstants.LEFT);
        JLabel qtyLbl = new JLabel("Quantity", SwingConstants.RIGHT);
        headPanel.add(foodLbl);
        headPanel.add(qtyLbl);
        headPanel.add(new JLabel(""));
        headPanel.add(new JLabel(""));
        contentPanel.add(headPanel);

        // Add initial food+qty rows
        for (int i = 0; i < foods.size(); ++i) {
            addFoodRow(contentPanel, foods.get(i), qtys.get(i));
        }

        // Add ingredient button
        JButton addBtn = new JButton("Add Ingredient");
        addBtn.addActionListener(e -> addFoodRow(contentPanel, "", ""));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton saveBtn = new JButton("Save Update");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setPreferredSize(new Dimension(110, 30));
        cancelBtn.setPreferredSize(new Dimension(90, 30));

        btnPanel.add(addBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        add(contentPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // Helper to add a row for ingredient editing
    private void addFoodRow(JPanel contentPanel, String food, String qty) {
        JPanel rowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);

        JTextArea foodField = new JTextArea(2, 15); // 2 rows for visibility
        foodField.setText(food);
        foodField.setLineWrap(true);
        foodField.setWrapStyleWord(true);

        JTextField qtyField = new JTextField(qty, 20);

        JButton removeBtn = new JButton("-");
        removeBtn.setPreferredSize(new Dimension(36, 25));
        removeBtn.setFont(removeBtn.getFont().deriveFont(Font.BOLD, 16f));
        removeBtn.addActionListener(e -> {
            contentPanel.remove(rowPanel);
            foodFields.remove(foodField);
            qtyFields.remove(qtyField);
            contentPanel.revalidate();
            contentPanel.repaint();
            pack();
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        rowPanel.add(foodField, gbc);
        gbc.gridx = 1;
        rowPanel.add(qtyField, gbc);
        gbc.gridx = 2;
        rowPanel.add(removeBtn, gbc);

        foodFields.add(foodField);
        qtyFields.add(qtyField);

        contentPanel.add(rowPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        pack();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<String> getNewFoods() {
        List<String> out = new ArrayList<>();
        for (JTextArea tf : foodFields) out.add(tf.getText().trim());
        return out;
    }

    public List<String> getNewQuantities() {
        List<String> out = new ArrayList<>();
        for (JTextField tf : qtyFields) out.add(tf.getText().trim());
        return out;
    }
}
