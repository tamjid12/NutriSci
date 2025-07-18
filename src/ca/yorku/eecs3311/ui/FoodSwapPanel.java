package ca.yorku.eecs3311.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import ca.yorku.eecs3311.meal.MealItem;

public class FoodSwapPanel extends JPanel {
    private final Navigator nav;
    private final List<MealItem> originalMeal;
    private JTextArea originalTextArea, swappedTextArea;
    private JComboBox<String> goalBox1;
    private JComboBox<String> goalBox2;

    public FoodSwapPanel(Navigator nav, List<MealItem> currentMeal) {
        this.nav = nav;
        this.originalMeal = currentMeal;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Food Swap Suggestions", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        swappedTextArea = new JTextArea();
        swappedTextArea.setEditable(false);

        updateOriginalMealDisplay();

        centerPanel.add(new JScrollPane(originalTextArea));
        centerPanel.add(new JScrollPane(swappedTextArea));
        add(centerPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        goalBox1 = new JComboBox<>(new String[]{"Select Goal", "Reduce Calories", "Reduce Sugar", "Increase Protein", "Increase Fiber"});
        goalBox2 = new JComboBox<>(new String[]{"Select Second Goal (optional)", "Reduce Calories", "Reduce Sugar", "Increase Protein", "Increase Fiber"});
        JButton swapBtn = new JButton("Swap");
        JButton backBtn = new JButton("Back");

        swapBtn.addActionListener(e -> performSwap());
        backBtn.addActionListener(e -> nav.showMealLog(null)); // or pass profileName if needed

        controlPanel.add(goalBox1);
        controlPanel.add(goalBox2);
        controlPanel.add(swapBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void updateOriginalMealDisplay() {
        StringBuilder sb = new StringBuilder("Original Meal:\n");
        for (MealItem item : originalMeal) {
            sb.append("- ").append(item.getFoodName()).append(" (g): ").append(item.getQuantity()).append("\n");
        }
        originalTextArea.setText(sb.toString());
    }

    private void performSwap() {
        String goal1 = (String) goalBox1.getSelectedItem();
        String goal2 = (String) goalBox2.getSelectedItem();

        List<MealItem> swapped = new ArrayList<>();
        for (MealItem item : originalMeal) {
            String name = item.getFoodName();
            double qty = item.getQuantity();

            // Simple hardcoded logic
            if (goal1.contains("Reduce Calories") || goal2.contains("Reduce Calories")) {
                if (name.equalsIgnoreCase("Bacon")) {
                    swapped.add(new MealItem(0, "Grilled Chicken", qty));
                    continue;
                }
            }
            if (goal1.contains("Increase Protein") || goal2.contains("Increase Protein")) {
                if (name.equalsIgnoreCase("White Bread")) {
                    swapped.add(new MealItem(0, "Whole Wheat Bread", qty));
                    continue;
                }
            }
            if (goal1.contains("Increase Fiber") || goal2.contains("Increase Fiber")) {
                if (name.equalsIgnoreCase("White Bread")) {
                    swapped.add(new MealItem(0, "Oatmeal", qty));
                    continue;
                }
            }
            if (goal1.contains("Reduce Sugar") || goal2.contains("Reduce Sugar")) {
                if (name.equalsIgnoreCase("Jam")) {
                    swapped.add(new MealItem(0, "Nut Butter (unsweetened)", qty));
                    continue;
                }
            }
            swapped.add(item); // default: no swap
        }

        StringBuilder sb = new StringBuilder("Swapped Meal:\n");
        for (MealItem item : swapped) {
            sb.append("- ").append(item.getFoodName()).append(" (g): ").append(item.getQuantity()).append("\n");
        }
        swappedTextArea.setText(sb.toString());
    }
}
