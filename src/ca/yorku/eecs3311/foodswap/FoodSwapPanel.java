package ca.yorku.eecs3311.foodswap;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ca.yorku.eecs3311.meal.MealItem;

public class FoodSwapPanel extends JPanel {
    private JComboBox<String> goalBox;
    private JButton suggestBtn;
    private JTextArea resultArea;

    private final FoodSwapController controller;
    private final List<MealItem> currentMeal;

    public FoodSwapPanel(List<MealItem> currentMeal) {
        this.currentMeal = currentMeal;
        Map<String, Double> mealMap = new HashMap<>();
        for (MealItem item : currentMeal) {
            mealMap.put(item.getFoodName(), item.getQuantity());
        }
        this.controller = new FoodSwapController(mealMap);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel goalLabel = new JLabel("Select Nutritional Goal:");
        goalBox = new JComboBox<>(new String[]{
                "Reduce Calories",
                "Increase Fiber",
                "Reduce Sugar",
                "Increase Protein"
        });

        suggestBtn = new JButton("Suggest Swap");
        resultArea = new JTextArea(12, 40);
        resultArea.setEditable(false);

        suggestBtn.addActionListener(this::onSuggestSwap);

        JPanel topPanel = new JPanel();
        topPanel.add(goalLabel);
        topPanel.add(goalBox);
        topPanel.add(suggestBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void onSuggestSwap(ActionEvent e) {
        String goal = (String) goalBox.getSelectedItem();
        Map<String, Double> swapped = controller.suggestSwap(goal, currentMeal);

        StringBuilder sb = new StringBuilder();
        sb.append("Original Meal:\n");
        currentMeal.forEach(item ->
                sb.append("- ").append(item.getFoodName()).append(" (g): ").append(item.getQuantity()).append("\n"));

        sb.append("\nSuggested Swap:\n");
        swapped.forEach((food, qty) ->
                sb.append("- ").append(food).append(" (g): ").append(qty).append("\n"));

        resultArea.setText(sb.toString());
    }
}
