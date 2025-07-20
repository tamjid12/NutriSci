package ca.yorku.eecs3311.ui;

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

import ca.yorku.eecs3311.foodswap.FoodSwapController;
import ca.yorku.eecs3311.foodswap.FoodSwapDAO;
import ca.yorku.eecs3311.foodswap.SwapInfo;
import ca.yorku.eecs3311.meal.MealItem;

public class FoodSwapPanel extends JPanel {
    private JComboBox<String> goalBox;
    private JButton suggestBtn;
    private JButton backBtn;
    private JTextArea resultArea;

    private final FoodSwapController controller;
    private final List<MealItem> currentMeal;
    private final Navigator nav;

    public FoodSwapPanel(List<MealItem> currentMeal, Navigator nav) {
        this.currentMeal = currentMeal;
        this.nav = nav;

        Map<String, Double> mealMap = new HashMap<>();
        for (MealItem item : currentMeal) {
            mealMap.put(item.getFoodName(), item.getQuantity());
        }
        this.controller = new FoodSwapController(mealMap);

        initUI();
        showOriginalMeal();
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
        backBtn = new JButton("Back");
        resultArea = new JTextArea(14, 50);
        resultArea.setEditable(false);

        suggestBtn.addActionListener(this::onSuggestSwap);
        backBtn.addActionListener(e -> {
            if (nav != null) nav.showMealLog("");
        });

        JPanel topPanel = new JPanel();
        topPanel.add(goalLabel);
        topPanel.add(goalBox);
        topPanel.add(suggestBtn);
        topPanel.add(backBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void showOriginalMeal() {
        StringBuilder sb = new StringBuilder();
        sb.append("Original Meal:\n");
        for (MealItem item : currentMeal) {
            SwapInfo info = controller.getNutrientSummary(item.getFoodName(), item.getQuantity());
            sb.append("- ").append(item.getFoodName())
              .append(": Quantity: ").append(info.getQuantity()).append(" g")
              .append(" | Calories: ").append(String.format("%.1f", info.getCalories())).append(" kCal")
              .append(" | Protein: ").append(String.format("%.1f", info.getProtein())).append(" g\n");
        }
        sb.append("\nSuggested Swap:\n(Press 'Suggest Swap' to see recommendations)");
        resultArea.setText(sb.toString());
    }

    private void onSuggestSwap(ActionEvent e) {
        String goal = (String) goalBox.getSelectedItem();
        FoodSwapDAO dao = new FoodSwapDAO();

        Map<String, Double> swapped = new HashMap<>();
        for (MealItem item : currentMeal) {
            List<String> suggestions = dao.suggestSwap(item.getFoodName(), goal);
            String replacement = suggestions.isEmpty() ? item.getFoodName() : suggestions.get(0);
            swapped.put(replacement, item.getQuantity());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Original Meal:\n");
        for (MealItem item : currentMeal) {
            SwapInfo info = controller.getNutrientSummary(item.getFoodName(), item.getQuantity());
            sb.append("- ").append(item.getFoodName())
              .append(": Quantity: ").append(info.getQuantity()).append(" g")
              .append(" | Calories: ").append(String.format("%.1f", info.getCalories())).append(" kCal")
              .append(" | Protein: ").append(String.format("%.1f", info.getProtein())).append(" g\n");
        }

        sb.append("\nSuggested Swaps:\n");
        for (Map.Entry<String, Double> entry : swapped.entrySet()) {
            SwapInfo info = controller.getNutrientSummary(entry.getKey(), entry.getValue());
            sb.append("- ").append(entry.getKey())
              .append(": Quantity: ").append(info.getQuantity()).append(" g")
              .append(" | Calories: ").append(String.format("%.1f", info.getCalories())).append(" kCal")
              .append(" | Protein: ").append(String.format("%.1f", info.getProtein())).append(" g\n");
        }

        resultArea.setText(sb.toString());
    }
}
