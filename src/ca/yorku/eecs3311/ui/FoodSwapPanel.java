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
    private final FoodSwapDAO dao = new FoodSwapDAO();
    private final Map<MealItem, MealItem> swapMap = new HashMap<>();

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
                "Increase Protein"
        });

        suggestBtn = new JButton("Suggest Swap");
        backBtn = new JButton("Back");
        resultArea = new JTextArea(18, 60);
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
            sb.append(String.format("- %s: Quantity: %.0f g | Calories: %.1f kCal | Protein: %.1f g\n",
                    item.getFoodName(),
                    item.getQuantity(),
                    info.getCalories(),
                    info.getProtein()));
        }
        sb.append("\nSuggested Swaps:\n(Press 'Suggest Swap' to see recommendations)");
        resultArea.setText(sb.toString());
    }

    private void onSuggestSwap(ActionEvent e) {
        swapMap.clear();
        String goal = (String) goalBox.getSelectedItem();

        StringBuilder sb = new StringBuilder();
        sb.append("Original Meal:\n");
        for (MealItem item : currentMeal) {
            SwapInfo originalInfo = controller.getNutrientSummary(item.getFoodName(), item.getQuantity());
            sb.append(String.format("- %s: Quantity: %.0f g | Calories: %.1f kCal | Protein: %.1f g\n",
                    item.getFoodName(),
                    item.getQuantity(),
                    originalInfo.getCalories(),
                    originalInfo.getProtein()));
        }

        sb.append("\nSuggested Swaps:\n");
        for (MealItem item : currentMeal) {
            double quantity = item.getQuantity();
            List<String> candidates = dao.suggestSwap(item.getFoodName(), goal);

            if (!candidates.isEmpty()) {
                String suggested = candidates.get(0);
                MealItem newItem = new MealItem(item.getId(), item.getEntryId(), suggested, quantity);
                swapMap.put(item, newItem);

                SwapInfo swapInfo = controller.getNutrientSummary(suggested, quantity);
                sb.append(String.format("- %s ➔ %s: %.0f g | Calories: %.1f kCal | Protein: %.1f g\n",
                        item.getFoodName(),
                        suggested,
                        quantity,
                        swapInfo.getCalories(),
                        swapInfo.getProtein()));
            }
        }

        resultArea.setText(sb.toString());
    }
}
