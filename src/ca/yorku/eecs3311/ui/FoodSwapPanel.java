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
import ca.yorku.eecs3311.meal.MealItem;
import ca.yorku.eecs3311.nutrient.FoodSwapDAO;

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
            if (nav != null) nav.showMealLog(""); // Make sure this is defined in your Navigator
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
        currentMeal.forEach(item ->
                sb.append("- ").append(item.getFoodName()).append(" (g): ").append(item.getQuantity()).append("\n"));
        sb.append("\nSuggested Swap:\n(Press 'Suggest Swap' to see recommendations)");
        resultArea.setText(sb.toString());
    }

    private void onSuggestSwap(ActionEvent e) {
        String goal = (String) goalBox.getSelectedItem();
        FoodSwapDAO dao = new FoodSwapDAO();

        Map<String, Double> swapped = new HashMap<>();
        for (MealItem item : currentMeal) {
            List<String> suggestions = dao.suggestSwap(item.getFoodName(), goal);
            String replacement = (!suggestions.isEmpty() && !suggestions.get(0).equalsIgnoreCase(item.getFoodName()))
                                 ? suggestions.get(0)
                                 : item.getFoodName();
            swapped.put(replacement, item.getQuantity());
        }

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
